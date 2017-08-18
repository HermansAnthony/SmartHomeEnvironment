package controller.clients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.AvroRemoteException;

import proto.ClientProto;

public class TemperatureSensor extends Client {
	private List<Float> temperatureHistory = new ArrayList<Float>();
	private int maxSize = 11;
	
	public TemperatureSensor(CharSequence IPaddress, int portNumber) throws IOException {
		super(IPaddress, portNumber);
		this.proxy = this.connection.connect(ClientProto.class, "");
	}
	
	public double getTemperature() throws AvroRemoteException {
		if (temperatureHistory.size() == 0) {
			return 0;
		}
		return temperatureHistory.get(temperatureHistory.size()-1);
	}
	
	public void addTemperature(float newValue) {
		temperatureHistory.add(newValue);
		if (temperatureHistory.size() >= maxSize)
			temperatureHistory.remove(0.0);
	}
	
	public void setTemperatureHistory(List<Float> history) {
		this.temperatureHistory = history;
	}

	public List<Float> getTemperatureHistory() {
		if (temperatureHistory.size() == 0) {
			List<Float> temp = new ArrayList<Float>();
			temp.add((float) 0.0);
			return temp;
		}
		return temperatureHistory;
	}
}
