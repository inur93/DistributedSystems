package sensorServer;

import common.Constants;

public class SensorServer implements Runnable{
	private String[] topic;
	public SensorServer(String[] topic){
		this.topic = topic;
	}

	@Override
	public void run() {
		int listenerPort = Constants.SUBSCRBER_PORT;
		int publisherPort = Constants.PUBLISHER_PORT;
	
		Subscriber s1 = new Subscriber(topic[0], listenerPort, publisherPort);
//		Subscriber s2 = new Subscriber(topic[1], listenerPort+1, publisherPort+1);
		new Thread(s1).start();
//		new Thread(s2).start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s1.addTopic(topic[1]);
		
	}
}
