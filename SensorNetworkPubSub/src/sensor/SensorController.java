package sensor;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;

import sensor.gui.SensorGUI;
import common.Broadcaster;
import common.Constants;
import common.Constants.Topics;


public class SensorController implements Runnable {
	public volatile LinkedList<String> queue = new LinkedList<String>();
	private volatile LinkedList<InetAddress> subscribers = new LinkedList<InetAddress>(); 
	private SensorGUI log;
	private Thread[] datagenThread;
	private Publisher publisher;
	private SubscriptionReceiver subscriptionReceiver;
	
	public volatile boolean terminate = false;


	private Topics topic;


	private volatile DatagramSocket senderSocket;
	private volatile DatagramSocket receiverSocket;

	public SensorController(Topics topic) {
		this.topic = topic;
		this.log = new SensorGUI(this);
		new Thread(this.log).start();
		try {
			this.senderSocket = new DatagramSocket();
			this.receiverSocket = new DatagramSocket(Constants.PUBLISHER_PORT);
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
			addMsgToLog(getClass().getSimpleName() + ">> publish: " + getTopic() + val);
			this.publisher.publish(getTopic() + val);
			
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
		
		
		new Thread(new Broadcaster(getTopic() + Constants.READY_EVENT, Constants.SUBSCRBER_PORT, this.log)).start();

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
		this.log.addMsg(msg.trim());
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
	
	public String getTopic(){
		return this.topic + ";";
	}

}
