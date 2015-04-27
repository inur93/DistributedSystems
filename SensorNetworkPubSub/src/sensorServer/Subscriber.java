package sensorServer;
import java.net.*;
import java.util.ArrayList;

import sensorServer.gui.ServerGUI;
import common.Constants;
import common.PropertyHelper;
import common.Receiver;
import common.Sender;
import common.Event;
import common.Topic;


public class Subscriber implements Runnable, ISubscriber{
	private Receiver receiver;
	private Sender sender;
	private Thread receiverThread;
	private ServerGUI log;
	private int listenerPort;

	private volatile boolean terminated;

	private DatagramSocket receiverSocket;
	private DatagramSocket senderSocket;
	private ArrayList<Topic> topics = new ArrayList<>();

	public Subscriber(Topic[] topics, int listenerPort){
		if(topics != null) {
			for(Topic t : topics){
			this.topics.add(t);
			}
		}
		this.listenerPort = listenerPort;
		this.log = new ServerGUI(this);
		new Thread(this.log).start();

	}

	public void run(){
		terminated = false;
		try {
			this.receiverSocket = new DatagramSocket(listenerPort, InetAddress.getByName("0.0.0.0"));
			this.senderSocket = new DatagramSocket();
		} catch (SocketException e) {
			this.log.addMsg(getClass().getSimpleName() + ">> socket exception");
		} catch (UnknownHostException e) {
			this.log.addMsg(getClass().getSimpleName()+ ">> unknown host exception");
		}
		this.sender = new Sender(this.senderSocket, this.log);
		broadcast();
		this.receiver = new Receiver(this, this.log, this.receiverSocket);
		this.receiverThread = new Thread(this.receiver);
		this.receiverThread.start();
		
		while(!this.terminated){
			try {
				Thread.sleep(Constants.SUBSCRIPTION_BROADCAST_INTERVAL);
			} catch (InterruptedException e) {
				this.log.addMsg(getClass().getSimpleName() + ">> interrupted broadcast sleep");
			}
			broadcast();
		}
	}

	private void broadcast(){
		for(Topic t : this.topics){
			this.sender.send(new Event(t, Constants.SUBSCRIBE_VALUE, t.port, true));
		}
	}

	public void shutDown(){
		terminated = true;
		for(Topic t : topics){
			this.sender.send(new Event(t, Constants.UNSUBSCRIBE_VALUE,  t.port, true));
		}
		this.receiver.terminate();
		while(this.receiverThread.isAlive());
		this.receiverSocket.close();
		log.addMsg(getClass().getSimpleName()+ ">> shutdown");
	}
	public void restartServer(){
		shutDown();
		log.addMsg(getClass().getSimpleName()+">> restarting...");
		run();
	}

	@Override
	public synchronized void receiveEvent(Event event){
		String topic = event.topic.topic;
		String value = event.value;
		int key = PropertyHelper.findLastIndex(topic);
		if(checkTopic(topic)){
			if(value.matches(Constants.TEMP_DATA_VALUE)){
				writeToProperty(topic, String.valueOf(key), value.replace(";", ""));
				key++;
			}else if(value.contains(Constants.READY_VALUE)){
				this.log.addMsg(getClass().getSimpleName() + " >> Sending event to: " + event.address);
				Event sendEvent = new Event(event.topic, Constants.SUBSCRIBE_VALUE, event.address, event.topic.port);
				subscribe(sendEvent);
			}
		}
	}

	private synchronized boolean checkTopic(String topic){
		for(Topic t : topics){
			if(t.topic.equals(topic)) return true;
		}
		return false;
	}

	@Override
	public void subscribe(Event event) {
		event.value = Constants.SUBSCRIBE_VALUE;
		this.sender.send(event);
	}

	@Override
	public void unsubscribe(Event event) {
		event.value = Constants.UNSUBSCRIBE_VALUE;
		this.sender.send(event);;

	}

	/**
	 * verifies input and write data to file
	 * @param key should be integer so the index can be used for calculating total and mean
	 * @param val char[] that will be verified, can have the form (\d+((.|,)(\d+))?)
	 * @return if not able to convert to float false will be returned else data will be saved and true returned
	 */
	public boolean writeToProperty(String filename, String key, String value){
		value = value.replace(',', '.');
		try{
			Float.valueOf(value);
		}catch(NumberFormatException e){
			return false;
		}		
		PropertyHelper.writeToProperty(filename, key, value);
		return true;
	}

	public synchronized void addTopic(Topic topic){
		if(topic != null){
			System.out.println(topic);
			topics.add(topic);
			sender.send(new Event(topic, Constants.SUBSCRIBE_VALUE, topic.port, true));
		}
	}
	public synchronized void removeTopic(String topic){
		topics.remove(topic);
	}



}
