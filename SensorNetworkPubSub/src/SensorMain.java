

import common.Constants;
import sensor.Sensor;

/**
 * 
 * @author Runi
 *
 */
public class SensorMain {

	public static void main(String[] args) {
			Sensor s1 = new Sensor(Constants.TEST_TEMP_TOPIC);
			new Thread(s1).start();
			Sensor s2 = new Sensor(Constants.TEST_LIGHT_TOPIC);
			new Thread(s2).start();
	}

}
