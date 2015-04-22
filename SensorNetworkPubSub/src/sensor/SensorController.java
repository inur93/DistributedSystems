package sensor;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;

public class SensorController implements Runnable {
	public volatile LinkedList<String> queue = new LinkedList<String>();
	private volatile LinkedList<InetAddress> subscribers = new LinkedList<InetAddress>(); 
	private SensorGUI gui;
	private Thread[] datagenThread;
	private Publisher publisher;
	private SubscriptionReceiver subscriptionReceiver;
	
	public volatile boolean terminate = false;

	public static final String DEFAULT_NAME =  "192.168.10.255";//"255.255.255.255"; //
	public static final int RECEIVER_PORT = 8889;
	public static final int PACKET_PORT = 8888;

	public static final String FILE_NAME = "temperature";
	
	public static final String TEMP_TOPIC = "TEMP;";

	public static final String BROADCAST_EVENT = "TEMP;READY;";
	
	public static final String SUBSCRIBE_EVENT = "TEMP;SUBSCRIBE;";

	public static final int PACKET_SIZE = 512;
	public static final int DATA_SIZE = 5;

	private volatile DatagramSocket senderSocket;
	private volatile DatagramSocket receiverSocket;

	public SensorController() {
		this.gui = new SensorGUI(this);
		new Thread(this.gui).start();
		try {
			this.senderSocket = new DatagramSocket();
			this.receiverSocket = new DatagramSocket(RECEIVER_PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public synchronized LinkedList<InetAddress> getSubscribers(){
		return (LinkedList<InetAddress>) this.subscribers.clone();
	}
	public synchronized void addSubscriber(InetAddress socketAddress){
		if(!subscribers.contains(socketAddress)) subscribers.add(socketAddress);
	}

	public synchronized void publish(){
		try{
		while(!queue.isEmpty() && !subscribers.isEmpty()){
			
			String val =this.queue.pop();
			addMsgToLog(getClass().getSimpleName() + ">> publish: " + val);
			this.publisher.publish(val);
			
		}
		}catch(Exception e){
			return;
		}

	}
	public void run(){
		//One thread for sending data
		terminate = false;
		this.publisher = new Publisher(this, senderSocket);
		this.publisher.start();

		this.subscriptionReceiver = new SubscriptionReceiver(this, receiverSocket);
		new Thread(this.subscriptionReceiver).start();
		
		
		new Thread(new Broadcaster(this)).start();

		//10 threads generating data
		this.datagenThread = new Thread[10];
		for(int i = 0; i < 10; i++){
		this.datagenThread[i] = new DataGenerator(this);
		double rand = Math.random() * 1000;
		try {
			Thread.sleep((long) rand);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.datagenThread[i].start();
		}

	}
	public synchronized void addMsgToLog(String msg){
		this.gui.addMsgToLog(msg);
	}
	
	public void shutdownSensor() {
		terminate = true;
		closeSockets();
		addMsgToLog("shutdown");
	}
	public void restartSensor() {
		terminate = true;
		closeSockets();
		addMsgToLog("restarting");
		run();
	}
	
	private void closeSockets(){
		if(this.receiverSocket != null && !this.receiverSocket.isClosed()) this.receiverSocket.close();
		if(this.senderSocket != null && !this.receiverSocket.isClosed()) this.senderSocket.close();
	}

}
