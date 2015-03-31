package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;

public interface ICalculator extends Remote {
	public static final String calcName = "Calculator";
	public static final String host = "localhost";
	public static final int port = 1099;
	public static final String fileName = "temperature";
	
	double calculateMean() throws RemoteException;
//	Timestamp getTimestamp() throws RemoteException;
}
