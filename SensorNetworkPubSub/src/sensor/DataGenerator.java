package sensor;

import java.util.Random;

public class DataGenerator extends Thread{
	Sensor ctrl;

	public DataGenerator(Sensor controller) {
		this.ctrl = controller;
	}
	@Override
	public void run() {
		while(!this.ctrl.isTerminated()){
			synchronized (this) {
				try {
					sleep(3000);
					String data = measure();
					ctrl.pushData(data);

				} catch (InterruptedException e) {
					e.printStackTrace();

				}
			}
		}
	}

	// Generate random temp between 14.0-24.0 degress Celsius
	String measure() {
		int low = 14;
		int high = 24;

		Random random = new Random();
		float temp = (float) (((high-low) * random.nextDouble())+low);

		return convert(temp);
	}

	// Convert to char array, in format '_''x''y''.''z''_'
	String convert(float temp) {
		return String.format("%.2f", temp);
	}



}
