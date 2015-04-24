package sensorServer;
import java.net.*;

import sensorServer.gui.ServerGUI;
import common.Broadcaster;
import common.Constants;
import common.Constants.Topics;
import common.PropertyHelper;


public class SensorServerController implements Runnable{
	private DataHandler dataHandler;
	private Subscriber subscriber;
	private Thread dataHandlerThread;
	private Thread subscriberThread;
	private ServerGUI log;
	
	public volatile boolean terminate = false;
	private DatagramSocket receiverSocket;
	private Topics topic;
	
	public SensorServerController(Topics topic){
		this.topic = topic;
		this.log = new ServerGUI(this);
		new Thread(this.log).start();
		try {
			this.receiverSocket = new DatagramSocket(Constants.SUBSCRBER_PORT, InetAddress.getByName("0.0.0.0"));
		} catch (SocketException e) {
			this.log.addMsg(getClass().getSimpleName() + ">> socket exception");
		} catch (UnknownHostException e) {
			this.log.addMsg(getClass().getSimpleName()+ ">> unknown host exception");
		}
	}
	
	public void sendSubscription(InetAddress address){
		this.subscriber.subscribe(address);
	}

	public void run(){
		terminate = false;
		new Thread(new Broadcaster(this.topic+";"+ Constants.SUBSCRIBE_EVENT, Constants.PUBLISHER_PORT, this.log)).start();
		this.subscriber = new Subscriber(topic+";", this.log);
		this.subscriberThread = new Thread(subscriber);
		this.subscriberThread.start();
		this.dataHandler = new DataHandler(this.subscriber, this.log, this.receiverSocket, topic + ";");
		this.dataHandlerThread = new Thread(this.dataHandler);
		this.dataHandlerThread.start();
	}
	
	public void shutDownServer(){
		this.dataHandler.terminate();
	}
	public void restartServer(){
		this.dataHandler.terminate();
		run();
	}
	
	

		
}
