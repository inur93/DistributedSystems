package sensorServer;
import java.net.*;

import common.PropertyHelper;


public class SensorServerController implements Runnable{
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
	private Thread dataHandlerThread;
	private Thread subscriberThread;
	
	
	
	private ServerGUIController guiCtrl;
	private DatagramSocket receiverSocket;
	private DatagramSocket senderSocket;
	private DatagramSocket listenerSocket;
	public SensorServerController(){
		this.guiCtrl = new ServerGUIController(this);
	}
	
	public void sendSubscription(InetAddress address){
		this.subscriber.sendSubscription(address);
	}

	public void run(){
		this.subscriber = new Subscriber(this);
		this.subscriberThread = new Thread(subscriber);
		this.subscriberThread.start();
		DataHandler dataHandler = new DataHandler(this);
		this.dataHandlerThread = new Thread(dataHandler);
		this.dataHandlerThread.start();
	}
	
	public void restartServer(){
		try{
		subscriberThread.interrupt();
		dataHandlerThread.interrupt();
		}catch(Exception e){
			guiCtrl.writeToLog(getClass().getSimpleName() + ">> restarting server...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		run();
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

	public synchronized void writeToLog(String msg) {
		this.guiCtrl.writeToLog(msg);
		
	}
}
