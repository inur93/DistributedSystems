package sensorServer;

import common.Constants;
import common.Topic;

public class SensorServer implements Runnable{
	private Topic[] topic;
	private ISubscriber subscriber;
	public SensorServer(Topic[] topic){
		this.topic = topic;
	}

	@Override
	public void run() {
	
		this.subscriber = new Subscriber(this.topic, Constants.SUBSCRIBER_PORT);
		new Thread(this.subscriber).start();
		
	}
}
