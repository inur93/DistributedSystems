
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import rmiServer.Calculator;
import sensorServer.SensorServerController;
import common.ICalculator;
import common.Constants.Topics;


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
		SensorServerController sensorServer = new SensorServerController(Topics.TEMP);
		Thread sensorServerThread = new Thread(sensorServer);
		sensorServerThread.start();
		System.out.println("Sensor Server started");
		
	}

}
