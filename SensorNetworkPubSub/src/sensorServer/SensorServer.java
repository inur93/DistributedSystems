package sensorServer;

import common.Constants;
import common.Topic;

public class SensorServer implements Runnable{
	private Topic[] topic;
	public SensorServer(Topic[] topic){
		this.topic = topic;
	}

	@Override
	public void run() {
	
		Subscriber s1 = new Subscriber(this.topic, Constants.SUBSCRIBER_PORT);
		new Thread(s1).start();
		
	}
}
