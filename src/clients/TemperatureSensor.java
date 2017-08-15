package clients;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.avro.AvroRemoteException;


public class TemperatureSensor extends Client {
	private float temperature = 0;
	private float driftValue = 0;
	private Random generator;
	private Thread clockThread = null;
	private long internalClock;
	Boolean running;
	private int timeBetweenMeasurements = 5;
	private long timeSinceLastTemperature;	// Contains a counter when the last temperature was sent (in seconds)
	private long Tr; 						// Time the request was sent
	private long Ta;						// Time the response was received
	
	private DateFormat formatter; 			// Formatter to print the time a readable format
	
	public TemperatureSensor() { 
		super();
		initialize();
		type = "TemperatureSensor"; 
		formatter =  new SimpleDateFormat("HH:mm:ss:SSS");
		running = true;
		internalClock = System.currentTimeMillis();
	}
	
	public void startClockThread(){
		this.running = true;
		clockThread = new Thread(new Runnable() {
			@Override
			public void run(){ timeStep();}
		});
		clockThread.start();
	}
	
	public void endClockThread(){
		this.running = false;
		try { clockThread.join(); } catch (InterruptedException e) {
			System.out.println("Something went wrong with thread join");
			e.printStackTrace();
		}
	}
	
	private void timeStep(){
		while(true && this.running){
			internalClock = internalClock + 1000 + (long) driftValue;
			try { Thread.sleep(1000); timeSinceLastTemperature+=1; } catch (InterruptedException e) {
				System.err.println("Something went wrong with sleeping of the clock thread");
				e.printStackTrace();
			}
			// After each timeBetweenMeasurements seconds => send a new temperature
			if (timeSinceLastTemperature % timeBetweenMeasurements == 0)
				try { addTemperature();} 
				catch (AvroRemoteException | EOFException | UndeclaredThrowableException e) {
					System.err.println("Connection was disconnected");
					this.running = false;
				}
		}
		// TODO maybe sync time in separate thread
		//clockSync();
	}
	
	public void clockSync(){
		System.out.println("Clock sync");
		try {
			Tr = System.currentTimeMillis();
			long serverTime = proxy.getServerTime(controllerConnection.getId());
			Ta = System.currentTimeMillis();
			long drifted = internalClock;
			internalClock = serverTime + (Ta-Tr)/2;
//			System.out.println("Tr: " + formatter.format(new Date(Tr)));
			System.out.println("Server time: "+ formatter.format(new Date(serverTime)));
//			System.out.println("Ta: " + formatter.format(new Date(Ta)));
			System.out.println("Before: " + formatter.format(new Date(drifted)) + " After: "+ formatter.format(new Date(internalClock)));
		} catch (AvroRemoteException e) {
			System.err.println("Something went wrong when fetching time from the server");
			e.printStackTrace();
		}
	}
	
	// Set the initial temperature and drift value	
	private void initialize(){
		generator = new Random();
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));     
        String input = "";
        while (input.equals("")) {
    		System.out.println("TemperatureSensor> Please provide the initial temperature.");
		    try {
	            while(!br.ready()) { Thread.sleep(200); }
	            input = br.readLine();
	    	} catch (IOException | InterruptedException e) {
	    		System.err.println("Input error.");
	    	    try { br.close(); } catch (IOException e1) {}
				System.exit(0);
			}	
		    try { temperature = Float.parseFloat(input); } catch (NumberFormatException e) { input = "";}
        }
	    input = "";
	    while (input.equals("")) {
    		System.out.println("TemperatureSensor> Please provide the drift value (in seconds).");
		    try {
	            while(!br.ready()) { Thread.sleep(200); }
	            input = br.readLine();
	    	} catch (IOException | InterruptedException e) {
	    		System.err.println("Input error.");
	    	    try { br.close(); } catch (IOException e1) {}
				System.exit(0);
			}	
		    try { driftValue = Float.parseFloat(input) * 1000; } catch (NumberFormatException e) { input = "";}
        }
	    try { br.close(); } catch (IOException e) {}
	}
	
	public void addTemperature() throws AvroRemoteException, EOFException, UndeclaredThrowableException {
		temperature = temperature - ( 1 - (2 * generator.nextFloat()) );
		proxy.addTemperature(controllerConnection.getId(), temperature);
	}
	
	private void getTemperature(){
		System.out.println(temperature + " degres celsius");
	}
	
	private void list() {
		System.out.println("id");
		System.out.println("Commands:");
		System.out.println("=========");
		System.out.println("temperature");
		System.out.println("exit");
		System.out.println("");
	}
	
	protected void handleInput(String input) throws AvroRemoteException {
		String[] command = input.split(" ");
        switch (command[0]) {
        	case "list":
        		list();
        		break;
        	case "temperature": 
        		getTemperature();
        		break;
        	default:
	        	System.out.println("Command not recognized. Type 'list' for more information.");
        }
	}
	
	public static void main(String[] args) {
		String serverIPAddress = "";
		int serverPortnumber = 6789;
		if (args.length > 0) {
			if (!args[0].equals("null"))
				serverIPAddress = args[0];
		}
		if (args.length > 1)	
			serverPortnumber = Integer.parseInt(args[1]);
		Entry<String, Integer> controllerDetails = new AbstractMap.SimpleEntry<>(serverIPAddress, serverPortnumber);
		while (!controllerDetails.getKey().equals("None")) 
			controllerDetails = new TemperatureSensor().run(controllerDetails);
	}
}
