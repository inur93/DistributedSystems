package rmiServer;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import common.ICalculator;
import common.PropertyHelper;

public class Calculator implements ICalculator {

	public Calculator() throws RemoteException {
		super();
	}


	/**
	 * @return mean temperature as double. If there is no data, mean.NaN will be returned
	 */
	public double calculateMean() throws RemoteException {
		int count = 0;
		double total = 0;
		while(true){
			String stringVal = PropertyHelper.readFromProperty(ICalculator.fileName, String.valueOf(count));
			try{
				total+= Double.valueOf(stringVal);
			}catch(NumberFormatException | NullPointerException e){
				break;
			}
			count++;
		}
		try{
			// calculates mean and runs some functions to get correct format
			return Double.valueOf((new DecimalFormat("#.##").format(total/count)).replace(",", "."));
		}catch(NumberFormatException e){
			return Double.NaN;
		}
	}

	public static void main(String[] args) {
		try {
			Calculator calc = new Calculator();
			ICalculator stub = (ICalculator) UnicastRemoteObject.exportObject(calc, 0);
			Registry registry = LocateRegistry.createRegistry(ICalculator.port);
			registry.bind(ICalculator.calcName, stub);
			System.out.println("RMIServer is running...");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


//	@Override
//	public Timestamp getTimestamp() throws RemoteException {
//		return PropertyHelper.getFileTimestamp();
//	}
	
//	public static void main(String[] args) throws RemoteException{
//		Calculator c = new Calculator();
//		double mean = c.calculateMean();
//		System.out.println("mean: " + mean);
//	}
	
	

}
