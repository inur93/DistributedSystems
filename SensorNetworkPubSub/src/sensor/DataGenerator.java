package sensor;

import java.util.Random;

public class DataGenerator extends Thread{
	SensorController ctrl;

	public DataGenerator(SensorController clientController) {
		this.ctrl = clientController;
	}
	@Override
	public void run() {
		while(true){
			synchronized (this) {
				try {
					sleep(3000);
					String data = measure();
					ctrl.queue.push(data);
					notify();
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
		String tempString = "_" + String.format("%.2f", temp) + "_";
		//char[] tempArray = tempString.toCharArray();

		return tempString;
	}



}
