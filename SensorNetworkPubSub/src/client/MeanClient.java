package client;

import java.awt.EventQueue;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import client.ClientWindow.ConnectionStatus;
import common.ICalculator;

public class MeanClient implements IMeanClient {

	private IClientWindow clientWindow;
	private ICalculator remoteObject;
	
	public static void main(String[] args) {
		MeanClient mc = new MeanClient();
		mc.run();
	}

	public void run() {
		showGui();
		connectServer();
	}
	
	public void calculateMean(){
		try {
			this.clientWindow.setMeanVal(this.remoteObject.calculateMean());
		} catch (Exception e) {
			this.clientWindow.setConnectionStatus(ConnectionStatus.FAILED);
			this.clientWindow.setMeanVal(Double.NaN);
		}
	}

	public void reConnect(){
		System.out.println(this.remoteObject);
		if(this.remoteObject == null){
			connectServer();
		}
	}
	private void connectServer() {
		try
		{
			System.out.println("Trying to Connect to RMIServer");
			clientWindow.setConnectionStatus(ConnectionStatus.CONNECTING);
			Registry registry = LocateRegistry.getRegistry("localhost", 1099);
			this.remoteObject = (ICalculator) registry.lookup(ICalculator.calcName);
			clientWindow.setConnectionStatus(ConnectionStatus.CONNECTED);
		} catch (RemoteException e) {
			clientWindow.setConnectionStatus(ConnectionStatus.FAILED);
			System.err.println("Not Able to connect to RMIServer");
		} catch (NotBoundException e) {
			System.err.println("No such remote object");
			clientWindow.setConnectionStatus(ConnectionStatus.FAILED);
		}
	}

	private void showGui(){
		this.clientWindow = new ClientWindow(this);
		EventQueue.invokeLater(this.clientWindow);
	}
}
