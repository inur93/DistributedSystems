package sensor;

import java.util.LinkedList;

public class SensorController implements Runnable {
	volatile LinkedList<String> queue = new LinkedList<>();

	private Thread datagenThread;
	private Publisher publisher;


	public SensorController() {

	}

	public void publish(){
		while(!queue.isEmpty()){
			String val =this.queue.pop();
			System.out.println("publishing: " + val);
			this.publisher.publish(val);
		}


	}
	public void run(){
		//One thread for sending data
		this.publisher = new Publisher();
		this.publisher.start();

		//One thread generating data
		this.datagenThread = new DataGenerator(this);
		this.datagenThread.start();

		// waits until publisher is ready before publishing data
		synchronized (publisher) {
			try {
				this.publisher.wait();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		// publishing when list is not empty
		while(true){
			synchronized (datagenThread) {
				try{
					System.out.println("waiting for measurements");
					// waiting for measurements to be added. assuming measurements queue is empty
					datagenThread.wait();
				}catch (InterruptedException e) {
					System.err.println("interruptexception");
				}
			}
			// publishing all measurements in queue
			publish();
		}

	}

}
