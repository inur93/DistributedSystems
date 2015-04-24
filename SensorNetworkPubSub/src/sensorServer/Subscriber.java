package sensorServer;
import java.net.*;
import java.util.ArrayList;

import sensorServer.gui.ServerGUI;
import common.Constants;
import common.PropertyHelper;
import common.Receiver;
import common.Sender;
import common.Event;


public class Subscriber implements Runnable, ISubscriber, IController{
	private Receiver receiver;
	private Sender sender;
	private Thread receiverThread;
	private Thread senderThread;
	private ServerGUI log;
	private int publisherPort;
	
	public volatile boolean terminate = false;
	private DatagramSocket receiverSocket;
	private ArrayList<String> topics = new ArrayList<>();
	
	public Subscriber(String topic, int listenerPort, int publisherPort){
		this.topics.add(topic);
		this.publisherPort = publisherPort;
		this.log = new ServerGUI(this);
		new Thread(this.log).start();
		try {
			this.receiverSocket = new DatagramSocket(listenerPort, InetAddress.getByName("0.0.0.0"));
		} catch (SocketException e) {
			this.log.addMsg(getClass().getSimpleName() + ">> socket exception");
		} catch (UnknownHostException e) {
			this.log.addMsg(getClass().getSimpleName()+ ">> unknown host exception");
		}
	}
	
	public void sendSubscription(InetAddress address){
		
	}

	public void run(){
		terminate = false;
		this.sender = new Sender(this.log);
		this.senderThread = new Thread(sender);
		this.senderThread.start();
		this.sender.send(new Event(this.topics.get(0), Constants.SUBSCRIBE_VALUE, this.publisherPort, true));
		this.receiver = new Receiver(this, this.log, this.receiverSocket);
		this.receiverThread = new Thread(this.receiver);
		this.receiverThread.start();
	}
	
	public void shutDownServer(){
		this.receiver.terminate();
	}
	public void restartServer(){
		this.receiver.terminate();
		run();
	}
	
	@Override
	public synchronized void receiveEvent(Event event){
		String topic = event.topic;
		String value = event.value;
		int key = PropertyHelper.findLastIndex(topic);
			if(topics.contains(topic)){
				if(value.matches("\\d{2}.\\d{2}")){
					writeToProperty(topic, String.valueOf(key), value.replace(";", ""));
					key++;
				}else if(value.contains(Constants.READY_EVENT.replace(";", ""))){
					this.log.addMsg(getClass().getSimpleName() + " >> Sending event to: " + event.address);
					Event sendEvent = new Event(topic, value, event.address, publisherPort);
					subscribe(sendEvent);
				}
			}
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
	
	public synchronized void addTopic(String topic){
		topics.add(topic);
		sender.send(new Event(topic, Constants.SUBSCRIBE_VALUE, publisherPort+1, true));
	}
	public synchronized void removeTopic(String topic){
		topics.remove(topic);
	}
	

		
}
