

import common.Topic;
import sensor.Sensor;

/**
 * 
 * @author Runi
 *
 */
public class SensorMain {

	public static void main(String[] args) {
			Sensor s1 = new Sensor(new Topic("TEMP", 8900));
			new Thread(s1).start();
			Sensor s2 = new Sensor(new Topic("LIGHT", 8901));
			new Thread(s2).start();
	}

}
