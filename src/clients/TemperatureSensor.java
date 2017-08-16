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
	private boolean running;
	private int timeBetweenMeasurements = 5;
	private int updateTime = 5;
	private DateFormat formatter; 			// Formatter to print the time a readable format
	
	public TemperatureSensor() { 
		super();
		initialize();
		type = "TemperatureSensor"; 
		formatter =  new SimpleDateFormat("HH:mm:ss:SSS");
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
		System.out.println("Ending clock thread");
		this.running = false;
		try { clockThread.join(); } catch (InterruptedException e) {
			System.out.println("Something went wrong with joining thread @Temperaturesensor");
		}
	}
	
	private void timeStep(){
		int counter = 0;
		while(this.running){
//			System.out.println("Running: " + this.running + " Counter: "+ counter);
			internalClock = internalClock + 1000 + (long) driftValue;
			try { Thread.sleep(1000); counter+=1; } catch (InterruptedException e) {
				System.err.println("Something went wrong with sleeping of the clock thread");
			}
			// After each timeBetweenMeasurements seconds => send a new temperature
			if (counter % timeBetweenMeasurements == 0) {
				System.out.println("Current time:" + formatter.format(new Date(internalClock)));
				try { addTemperature();} 
				catch (AvroRemoteException | EOFException | UndeclaredThrowableException e) {
					System.err.println("Connection was disconnected, waiting to reconnect");
				}
			}
			//After each updateTime seconds => sync the clock (Cristian's algorythm)
			if (counter % updateTime == 0){
				this.clockSync();
			}
		}
	}
	
	public void clockSync(){
		try {
			long Tr = System.currentTimeMillis(); 	// Time the request was sent
			long serverTime = proxy.getServerTime();
			long Ta = System.currentTimeMillis();	// Time the reply was received
			long drifted = internalClock;
			internalClock = serverTime + (Ta-Tr)/2;
			System.out.println("Before sync: " + formatter.format(new Date(drifted)) + " After sync: "+ formatter.format(new Date(internalClock)));
		} catch (AvroRemoteException e) {}
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
	
	private void changeUpdateTime(int newUpdateInterval){
		this.updateTime = newUpdateInterval;
	}
	
	private void list() {
		System.out.println("Commands:");
		System.out.println("=========");
		System.out.println("id");
		System.out.println("temperature");
		System.out.println("changeUT [newInterval]");
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
        	case "changeUT":
        		if (command.length > 1){
        			try { 
        				int newUpdateInterval = Integer.parseInt(command[1]); 
        				changeUpdateTime(newUpdateInterval);
        			} catch (NumberFormatException e) { 
        	        	System.out.println("The specified update interval is not convertable to integer");
        			}
        		}
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
