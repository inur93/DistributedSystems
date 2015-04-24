package sensor;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;

import sensor.gui.SensorGUI;
import sensorServer.IController;
import common.Constants;
import common.Receiver;
import common.Event;
import common.Sender;


public class Publisher implements Runnable, IPublisher, IController{
	public volatile LinkedList<Event> queue = new LinkedList<Event>();
	private volatile LinkedList<InetAddress> subscribers = new LinkedList<InetAddress>(); 
	private SensorGUI log;

	private Receiver receiver;
	private Sender sender;

	public volatile boolean terminate = false;


	private String topic;
	private int subscriberPort;

	private volatile DatagramSocket senderSocket;
	private volatile DatagramSocket receiverSocket;

	public Publisher(String topic, int listenerPort, int subscriberPort) {
		this.topic = topic;
		this.subscriberPort = subscriberPort;
		this.log = new SensorGUI(this);
		new Thread(this.log).start();
		try {
			this.senderSocket = new DatagramSocket();
			this.receiverSocket = new DatagramSocket(listenerPort);
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
	
	public synchronized void removeSubscriber(InetAddress address){
		subscribers.remove(address);
	}

	public synchronized void publish(Event event){

		queue.add(event);
		while(!queue.isEmpty() && !subscribers.isEmpty()){
			for(InetAddress address : subscribers){
				Event e = queue.pop();
				e.address = address;
				e.port = this.subscriberPort;
				sender.send(e);
			}

		}

	}
	public void run(){
		//One thread for receiving data	
		this.receiver = new Receiver(this, log, receiverSocket);
		new Thread(this.receiver).start();

		//One thread for sending data
		this.sender = new Sender(this.log);
		new Thread(this.sender).start();
		this.sender.send(new Event(this.topic, Constants.READY_EVENT, this.subscriberPort, true));


	}


	public void shutdownSensor() {
		terminate = true;
		closeSockets();
		this.log.addMsg("shutdown");
	}
	public void restartSensor() {
		terminate = true;
		closeSockets();
		this.log.addMsg("restarting");
		run();
	}

	private void closeSockets(){
		if(this.receiverSocket != null && !this.receiverSocket.isClosed()) this.receiverSocket.close();
		if(this.senderSocket != null && !this.receiverSocket.isClosed()) this.senderSocket.close();
	}


	@Override
	public void receiveEvent(Event event) {
		this.log.addMsg(getClass().getSimpleName() + ">> event received: " + event.toString());
		if(event.topic.equals(this.topic)){
				log.addMsg(getClass().getSimpleName() + ">> topic match: " + event.topic);
				log.addMsg("matching: " + event.value + " : " + Constants.SUBSCRIBE_VALUE);
			if(event.value.equals(Constants.SUBSCRIBE_VALUE)){
				addSubscriber(event.address);
				log.addMsg(getClass().getSimpleName() + ">> added subscriber: " + event.address);	
			}else if(event.value.equals(Constants.UNSUBSCRIBE_VALUE)){
				removeSubscriber(event.address);
				log.addMsg(getClass().getSimpleName() + ">> removed subscriber: " + event.address);	
			}
		}
	}

}
