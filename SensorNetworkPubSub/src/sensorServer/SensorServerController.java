package sensorServer;
import java.net.*;

import common.Broadcaster;
import common.Constants;
import common.Constants.Topics;
import common.PropertyHelper;


public class SensorServerController implements Runnable{
	private Subscriber subscriber;
	private Thread dataHandlerThread;
	private Thread subscriberThread;
	
	
	
	private ServerGUI gui;
	public volatile boolean terminate = false;
	private DatagramSocket receiverSocket;
	private Topics topic;
	public SensorServerController(Topics topic){
		this.topic = topic;
		this.gui = new ServerGUI(this);
		new Thread(this.gui).start();
		try {
			this.receiverSocket = new DatagramSocket(Constants.SUBSCRBER_PORT, InetAddress.getByName("0.0.0.0"));
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendSubscription(InetAddress address){
		this.subscriber.subscribe(address);
	}

	public void run(){
		terminate = false;
		new Thread(new Broadcaster(this.topic+";"+ Constants.SUBSCRIBE_EVENT, Constants.PUBLISHER_PORT)).start();
		this.subscriber = new Subscriber(topic+";", this);
		this.subscriberThread = new Thread(subscriber);
		this.subscriberThread.start();
		DataHandler dataHandler = new DataHandler(this, this.receiverSocket, topic + ";");
		this.dataHandlerThread = new Thread(dataHandler);
		this.dataHandlerThread.start();
	}
	
	public void shutDownServer(){
		terminate = true;
	}
	public void restartServer(){
		terminate = true;
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
		PropertyHelper.writeToProperty(Constants.FILE_NAME, key, value);
		return true;
	}

	public synchronized void writeToLog(String msg) {
		this.gui.addMsgToLog(msg.trim());
	}
	
	
	
}
