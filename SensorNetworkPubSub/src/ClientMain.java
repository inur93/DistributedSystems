import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import common.ICalculator;


/**
 * @author Christain og Runi
 * Klasse til at starte en RMI- klient, der forsøger at forbinde 20 gange til RMI-server
 */
public class ClientMain {

	public static void main(String[] args) {
		for (int i = 0;i<20;i++){

			try
			{
				System.out.println("Trying to Connect to RMIServer");
				Registry registry = LocateRegistry.getRegistry("localhost", 1099);
				ICalculator stub = (ICalculator) registry.lookup(ICalculator.calcName);
				System.out.println("mean: " + stub.calculateMean());
			} catch (RemoteException e) {
				e.printStackTrace();
				System.err.println("Not Able to connect to RMIServer");
			} catch (NotBoundException e) {
				System.err.println("No such remote object");
				e.printStackTrace();
			}
			delay(1000);
		}
		System.err.println("Client terminated");
	}


	private static void delay(int delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
