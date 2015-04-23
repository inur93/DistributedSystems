

import common.Constants.Topics;

import sensor.SensorController;

/**
 * 
 * @author Runi
 *
 */
public class SensorMain {

	public static void main(String[] args) {
			SensorController s = new SensorController(Topics.TEMP);
			new Thread(s).start();
	}

}
