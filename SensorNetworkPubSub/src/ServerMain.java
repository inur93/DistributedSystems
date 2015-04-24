
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import rmiServer.Calculator;
import sensorServer.SensorServer;
import sensorServer.Subscriber;
import common.Constants;
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
		SensorServer sensorServer = new SensorServer(new String[]{Topics.TEMP.toString(), Topics.LIGHT.toString()});
		Thread sensorServerThread = new Thread(sensorServer);
		sensorServerThread.start();
		System.out.println("Sensor Server started");
		
	}

}
