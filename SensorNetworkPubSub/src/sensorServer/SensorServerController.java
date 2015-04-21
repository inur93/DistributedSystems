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
	
	public static final String TEMP_TOPIC = "TEMP;";
	public static final String TEMP_READY = "READY;";
	public static final String TEMP_BROADCAST_EVENT = "TEMP;READY;";
	public static final String TEMP_SUBSCRIBE_EVENT = "TEMP;SUBSCRIBE;";
	
	public static final String FILE_NAME = "temperature";
	public static final int DATA_SIZE = 5;


	private Thread dataHandlerThread;
	private Thread subscriberThread;
	
	
	
	private ServerGUI gui;
	public volatile boolean terminate = false;
	private DatagramSocket receiverSocket;
	private DatagramSocket senderSocket;
	private DatagramSocket listenerSocket;
	public SensorServerController(){
		this.gui = new ServerGUI(this);
		new Thread(this.gui).start();
		try {
			this.receiverSocket = new DatagramSocket(SensorServerController.RECEIVER_PORT, InetAddress.getByName("0.0.0.0"));
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendSubscription(InetAddress address){
		this.subscriber.sendSubscription(address);
	}

	public void run(){
		terminate = false;
		this.subscriber = new Subscriber(TEMP_TOPIC, this);
		this.subscriberThread = new Thread(subscriber);
		this.subscriberThread.start();
		DataHandler dataHandler = new DataHandler(this, this.receiverSocket);
		this.dataHandlerThread = new Thread(dataHandler);
		this.dataHandlerThread.start();
	}
	
	public void shutDownServer(){
		terminate = true;
	}
	public void restartServer(){
		terminate = true;
		
//		try{
//		subscriberThread.interrupt();
//		dataHandlerThread.interrupt();
//		}catch(Exception e){
//			gui.addMsgToLog(getClass().getSimpleName() + ">> restarting server...");
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}
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
		this.gui.addMsgToLog(msg);
	}
	
}
