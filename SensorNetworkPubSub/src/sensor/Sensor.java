package sensor;

import common.Constants;
import common.Event;

public class Sensor implements Runnable {

	public volatile boolean terminate = false;
	private Thread[] datagenThread;
	private Publisher publisher;
	private String topic;
	
	public Sensor(String topic, int listenerPort){
		this.topic = topic;
		this.publisher = new Publisher(topic, listenerPort, Constants.SUBSCRBER_PORT);
		new Thread(this.publisher).start();
	}

	@Override
	public void run(){
	
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
				System.err.println("datagenerators started");
				
	}
	
	public synchronized void pushData(String value){
		this.publisher.publish(new Event(this.topic, value, 0, false));	
	}
	
	

}
