
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import rmiServer.Calculator;
import rmiServer.ICalculator;
import sensorServer.SensorServer;
import common.Constants;
import common.Topic;


public class ServerMain {

	public static void main(String[] args) {
		//Start RMI server
		try {
			Calculator calc = new Calculator();
			ICalculator stub = (ICalculator) UnicastRemoteObject.exportObject(calc, 0);
			Registry registry = LocateRegistry.createRegistry(ICalculator.port);
			registry.bind(ICalculator.calcName, stub);
			System.out.println("RMIServer is running...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("RMIServer started");
		
		//Start sensor server
		SensorServer sensorServer = new SensorServer(new Topic[]{Constants.TEST_LIGHT_TOPIC, Constants.TEST_TEMP_TOPIC});
		Thread sensorServerThread = new Thread(sensorServer);
		sensorServerThread.start();
		System.out.println("Sensor Server started");
		
	}

}
