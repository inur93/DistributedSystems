package sensor;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;

import sensor.gui.SensorGUI;
import sensorServer.IController;
import common.Constants;
import common.Receiver;
import common.Event;
import common.Sender;
import common.Subscription;
import common.Topic;


public class Publisher implements Runnable, IPublisher, IController{
	public boolean terminate;
	
	public volatile LinkedList<Event> queue = new LinkedList<Event>();
	private volatile LinkedList<Subscription> subscribers = new LinkedList<Subscription>(); 
	private SensorGUI log;

	private Receiver receiver;
	private Thread  receiverThread;
	private Sender sender;

	private Topic topic;
	private int subscriberPort;

	private volatile DatagramSocket senderSocket;
	private volatile DatagramSocket receiverSocket;

	public Publisher(Topic topic, int subscriberPort) {
		this.topic = topic;
		this.subscriberPort = subscriberPort;
		this.log = new SensorGUI(this, "Publisher: " + topic);
		new Thread(this.log).start();
		
	}

	public void run(){
		terminate = false;
		try {
			this.senderSocket = new DatagramSocket();
			this.receiverSocket = new DatagramSocket(topic.port);
		} catch (SocketException e) {
			log.addMsg(getClass().getSimpleName() + ">> can not continue. port is already in use");
		}
		log.addMsg(getClass().getSimpleName() + ">> listening on port: " + topic.port);
		
		//One thread for receiving data	
		this.receiver = new Receiver(this, log, receiverSocket);
		receiverThread = new Thread(this.receiver);
		receiverThread.start();

		//One thread for sending data
		this.sender = new Sender(senderSocket, this.log);
		this.sender.send(new Event(this.topic, Constants.READY_VALUE, subscriberPort, true));


	}
	
	@SuppressWarnings("unchecked")
	public synchronized LinkedList<Subscription> getSubscribers(){
		return (LinkedList<Subscription>) this.subscribers.clone();
	}
	public synchronized void addSubscriber(InetAddress socketAddress){
		boolean updated = false;
		for(Subscription s : subscribers){
			if(s.address.equals(socketAddress)){
				s.timestamp = new Timestamp(new Date().getTime());
				log.addMsg(getClass().getSimpleName() + ">> updated subscriber: " + socketAddress);
				updated = true;
			}
		}
		if(!updated) {
			subscribers.add(new Subscription(socketAddress, new Timestamp(new Date().getTime())));
			log.addMsg(getClass().getSimpleName() + ">> added subscriber: " + socketAddress);
		}
	}
	
	public synchronized void removeSubscriber(InetAddress address){
		subscribers.remove(address);
	}

	public synchronized void publish(Event event){

		queue.add(event);
		while(!queue.isEmpty() && !subscribers.isEmpty()){
			for(Subscription s : subscribers){
				Event e = queue.pop();
				e.address = s.address;
				sender.send(e);
				if(new Timestamp(new Date().getTime()).getTime() - s.timestamp.getTime() > Constants.SUBSCRIPTION_TIMEOUT){
					subscribers.remove(s);
					this.log.addMsg(getClass().getSimpleName() + ">> removed: " + s.toString());
				}
			}

		}

	}


	public void shutdownSensor() {
		this.terminate = true;
		this.receiver.terminate();
		closeSockets();
		while(this.receiverThread.isAlive()){};
		this.log.addMsg("shutdown");
	}
	public void restartSensor() {
		shutdownSensor();
		this.log.addMsg("restarting...");
		run();
	}

	private void closeSockets(){
		if(this.receiverSocket != null && !this.receiverSocket.isClosed()) this.receiverSocket.close();
		if(this.senderSocket != null && !this.receiverSocket.isClosed()) this.senderSocket.close();
	}


	@Override
	public void receiveEvent(Event event) {
		if(event.topic.topic.equals(this.topic.topic)){
				log.addMsg("matching: " + event.value + " : " + Constants.SUBSCRIBE_VALUE);
			if(event.value.equals(Constants.SUBSCRIBE_VALUE)){
				addSubscriber(event.address);	
			}else if(event.value.equals(Constants.UNSUBSCRIBE_VALUE)){
				removeSubscriber(event.address);
				log.addMsg(getClass().getSimpleName() + ">> removed subscriber: " + event.address);	
			}
		}
	}

}
