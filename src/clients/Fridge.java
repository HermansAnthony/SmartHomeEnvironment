package clients;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.avro.AvroRemoteException;


public class Fridge extends Client {
	private static Set<String> inventory = new HashSet<String>();
	private int currentUserId;
	
	public Fridge() { 
		super();
		type = "Fridge"; 
		currentUserId = -100;
	}
			
	private void list() {
		System.out.println("Commands:");
		System.out.println("=========");
		System.out.println("id");
		System.out.println("inventory");
		System.out.println("exit");
		System.out.println("");
	}
	
	private void inventory(){
        for(CharSequence item : inventory) 
            System.out.println(item);
	}

	protected void handleInput(String input) throws AvroRemoteException {
		String[] command = input.split(" ");
        switch (command[0]) {
        	case "list":
        		list();
        		break;
        	case "inventory": 
        		inventory();
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
			controllerDetails = new Fridge().run(controllerDetails);
	}

	@Override
	public List<CharSequence> getInventory() throws AvroRemoteException {
		List<CharSequence> temp = new ArrayList<CharSequence>();
		temp.addAll(inventory);
		return temp;
	}
	
	@Override
	public boolean addFridgeItem(CharSequence item) throws AvroRemoteException {
		return inventory.add(item.toString());
	}
	
	@Override 
	public boolean setFridgeUser(int userId) throws AvroRemoteException {
		this.currentUserId = userId;
		return true;
	}
	
	@Override
	public int isOpen() throws AvroRemoteException {
		return this.currentUserId;
	}

	@Override
	public boolean removeFridgeItem(CharSequence item)
			throws AvroRemoteException {
		boolean retVal = inventory.remove(item.toString());
		if (inventory.isEmpty())
			proxy.announceEmpty(controllerConnection.getId());
		return retVal;
	}

}
