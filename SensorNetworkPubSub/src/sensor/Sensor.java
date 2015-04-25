package sensor;

import common.Constants;
import common.Event;
import common.Topic;

public class Sensor implements Runnable {

	private Thread[] datagenThread;
	private IPublisher publisher;
	private Topic topic;

	public Sensor(Topic topic){
		this.topic = topic;
		this.publisher = new Publisher(topic, Constants.SUBSCRIBER_PORT);
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
		System.out.println(getClass().getSimpleName() + ">>datagenerators started");

	}

	public synchronized void pushData(String value){
		
			this.publisher.publish(new Event(this.topic, value, Constants.SUBSCRIBER_PORT, false));	
		
	}
	
	public boolean isTerminated(){
		return this.publisher.isTerminated();
	}



}
