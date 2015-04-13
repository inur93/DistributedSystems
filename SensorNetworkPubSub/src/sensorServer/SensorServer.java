package sensorServer;
import java.net.*;

import common.PropertyHelper;


public class SensorServer implements Runnable{
	private Subscriber subscriber;

	public static final int PACKET_PORT = 8889;
	public static final int RECEIVER_PORT = 8888;
	public static final int SUBSCRIPTION_PORT = 1234;
	public static final int PACKET_SIZE = 512;
	
	public static final String DEFAULT_NAME = "255.255.255.255";
	
	public static final String TEMPERATURE_SENSOR_READY_MSG = "NEW_TEMPERATURE_SENSOR_READY";
	public static final String TEMPERATURE_SENSOR_DATA_VAL = "TEMP_DATA";
	public static final String SUBSCRIBE_MSG = "SUBSCRIBE_TEMPERATURE_SENSOR";
	public static final String SUBSCRIPTION_RECEIVED_MSG = "SUBSCRIPTION_ACCEPTED";
	
	public static final String FILE_NAME = "temperature";
	public static final int DATA_SIZE = 5;

	
	
	public void sendSubscription(InetAddress address){
		this.subscriber.sendSubscription(address);
	}

	public void run(){
		this.subscriber = new Subscriber();
		new Thread(subscriber).start();
		
		DataHandler dataHandler = new DataHandler(this);
		new Thread(dataHandler).start();
	}

	/**
	 * verifies input and write data to file
	 * @param key should be integer so the index can be used for calculating total and mean
	 * @param val char[] that will be verified, can have the form (\d+((.|,)(\d+))?)
	 * @return if not able to convert to float false will be returned else data will be saved and true returned
	 */
	public boolean writeToProperty(String key, String value){
		value = value.replace(',', '.');
		try{
			Float.valueOf(value);
		}catch(NumberFormatException e){
			return false;
		}		
		PropertyHelper.writeToProperty(FILE_NAME, key, value);
		return true;
	}
}
