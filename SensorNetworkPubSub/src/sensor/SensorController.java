package sensor;

import java.net.InetAddress;
import java.util.LinkedList;

public class SensorController implements Runnable {
	public volatile LinkedList<String> queue = new LinkedList<String>();
	private volatile LinkedList<InetAddress> subscribers = new LinkedList<InetAddress>(); 
	private SensorGUI gui;
	private Thread datagenThread;
	private Publisher publisher;
	private Thread subscriptionReceiver;

	public static final String DEFAULT_NAME = "255.255.255.255";
	public static final int RECEIVER_PORT = 8889;
	public static final int PACKET_PORT = 8888;
	public static final int ACK_SENDER_PORT = 1234;

	public static final String FILE_NAME = "temperature";

	public static final String TEMPERATURE_SENSOR_READY_MSG = "NEW_TEMPERATURE_SENSOR_READY";
	public static final String TEMPERATURE_SUBSCRIBE_MSG = "SUBSCRIBE_TEMPERATURE_SENSOR";
//	public static final String SUBSCRIPTION_ACCEPTED_MSG = "SUBSCRIPTION_ACCEPTED";
	public static final String TEMPERATURE_SENSOR_DATA_VAL = "TEMP_DATA";

	public static final int PACKET_SIZE = 512;
	public static final int DATA_SIZE = 5;


	public SensorController() {
		this.gui = new SensorGUI(this);
		new Thread(this.gui).start();
	}
	@SuppressWarnings("unchecked")
	public synchronized LinkedList<InetAddress> getSubscribers(){
		return (LinkedList<InetAddress>) this.subscribers.clone();
	}
	public synchronized void addSubscriber(InetAddress socketAddress){
		if(!subscribers.contains(socketAddress)) subscribers.add(socketAddress);
	}

	public synchronized void publish(){
		while(!queue.isEmpty() && !subscribers.isEmpty()){
			String val =this.queue.pop();
			addMsgToLog(getClass().getSimpleName() + ">> publish: " + val);
			this.publisher.publish(val);
		}


	}
	public void run(){
		//One thread for sending data
		this.publisher = new Publisher(this);
		this.publisher.start();

		this.subscriptionReceiver = new SubscriptionReceiver(this);
		this.subscriptionReceiver.start();

		//One thread generating data
		this.datagenThread = new DataGenerator(this);
		this.datagenThread.start();

	}
	public synchronized void addMsgToLog(String msg){
		this.gui.addMsgToLog(msg);
	}
	
	public void shutdownSensor() {
		// TODO Auto-generated method stub
		
	}
	public void restartSensor() {
		// TODO Auto-generated method stub
		
	}

}
