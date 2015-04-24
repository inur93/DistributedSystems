

import common.Constants;
import common.Constants.Topics;
import sensor.Sensor;

/**
 * 
 * @author Runi
 *
 */
public class SensorMain {

	public static void main(String[] args) {
			Sensor s1 = new Sensor(Topics.TEMP.toString(), Constants.PUBLISHER_PORT);
			new Thread(s1).start();
			Sensor s2 = new Sensor(Topics.LIGHT.toString(), Constants.PUBLISHER_PORT+1);
			new Thread(s2).start();
	}

}
