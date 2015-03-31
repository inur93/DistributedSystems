import java.util.LinkedList;

import sensor.SensorController;


public class SensorMain {
	static String server = "127.0.0.1"; //args[0];
	static int port = 9999; //Integer.parseInt(args[1]);
	static volatile LinkedList<String> queue = new LinkedList<>();
	static final String fileName = "temperature";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub Start a number of Sensors
		SensorController[] sensors = new SensorController[10];
		for (SensorController s : sensors){
		s = new SensorController(server,port);
		new Thread(s).start();
		}
		

	}

}
