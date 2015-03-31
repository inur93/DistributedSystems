package sensorServer;
import java.net.*;
import java.util.Enumeration;
import java.io.*;

import common.PropertyHelper;


public class SensorServer implements Runnable{
	private DatagramSocket socket;
	private static final String DEFAULT_NAME = "255.255.255.255";
	private static final int DEFAULT_PORT = 8888;
	
	public static final String FILE_NAME = "temperature";
	public static final int DATA_SIZE = 5;
	public static void main(String[] args){ 
		Thread discoveryThread = new Thread(DiscoveryThread.getInstance());
		discoveryThread.start();
		SensorServer s = new SensorServer();
		s.run();
	}

	public void run(){
		
		try{
		socket = new DatagramSocket();
		socket.setBroadcast(true);
		byte[] data = "TEST_SENDING".getBytes();
		try{
			DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(DEFAULT_NAME), DEFAULT_PORT);
			socket.send(packet);
			System.out.println(getClass().getName() + " >> Request sent to: " + DEFAULT_NAME);
		}catch(Exception e){
			System.err.println("failed to send packet");
		}
		
		Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
		while(interfaces.hasMoreElements()){
			NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
			
			if(networkInterface.isLoopback() || !networkInterface.isUp()){
				continue;
			}
			
			for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()){
				InetAddress broadcast = interfaceAddress.getBroadcast();
				if(broadcast == null){
					continue;
				}
				
				try{
					DatagramPacket packet = new DatagramPacket(data, data.length, broadcast, DEFAULT_PORT);
					socket.send(packet);
				}catch(Exception e){
					System.err.println("failed to broadcast packet");
				}
				
				System.out.println(getClass().getName() + " >> Request sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
				
			}
		}
		
		System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

		byte[] receiveBuffer = new byte[15000];
		DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
		socket.receive(receivePacket);
		
		System.out.println(getClass().getName() + ">> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
		
		String msg = new String(receivePacket.getData()).trim();
		if(msg.equals("TEST_RECEIVE")){
			System.out.println("server ip address to store: " + receivePacket.getAddress());
		}
		
		socket.close();
	}catch(IOException e){
		
	}
		
//		ServerSocket sensorSocket = null;
//		Socket clientSocket = null;
//		DataInputStream inputStream = null;
//		try{
//			sensorSocket = new ServerSocket(9999);
//			System.out.println("Socket ready");
//		} catch(IOException e){
//			System.err.println("failed to create socket: " + e.getMessage());
//		}
//		int index = PropertyHelper.findLastIndex();
//		while(true){
//			try{
//				clientSocket = sensorSocket.accept();			
//				System.out.println("client connected");
//				inputStream = new DataInputStream(clientSocket.getInputStream());
//
//				char in;
//				int charNo = 0;
//				char[] temp = new char[DATA_SIZE];
//				// waiting for data
//				while((in = (char) inputStream.readByte()) == '_'){}
//				do{
//					// if trying to write data longer than 5 chars at a time
//					// or if data starting with ',' or '.' breaks and runs try clause again
//					if(charNo >= DATA_SIZE){
//						System.err.println("wrong data format received");
//						break; // goes outside whole try catch statement and closes client connection
//					}
//					temp[charNo] = in;
//					charNo++;
//				}while((in = (char) inputStream.readByte()) != '_');
//
//				// writing temperature measurement to file 
//				if(writeToProperty(String.valueOf(index), temp)){
//					index++;	
//				}
//
//
//			}catch(IOException e){
//				System.err.println("failed to connect to client: " + e.getMessage());
//			}
//
//			try {
//				if(!clientSocket.isConnected()){
//					clientSocket.close();	
//				}
//			} catch (IOException e) {
//				System.err.println("could not close client connection: " + e.getMessage());
//				break;
//			}
//		}	
//		try {
//			if(!sensorSocket.isClosed()){
//				sensorSocket.close();
//			}
//		} catch (IOException e) {
//			System.err.println("serversocket failed to close: " + e.getMessage());
//		}
	}

	/**
	 * verifies input and write data to file
	 * @param key should be integer so the index can be used for calculating total and mean
	 * @param val char[] that will be verified, can have the form (\d+((.|,)(\d+))?)
	 * @return if not able to convert to float false will be returned else data will be saved and true returned
	 */
	private boolean writeToProperty(String key, char[] val){
		String value = "";
		for(int i = 0; i<val.length;i++){
			if(val[i] != '\u0000'){
				value = value + val[i];
			}
		}
		value = value.replace(',', '.');
		try{
			Float.valueOf(value);
		}catch(NumberFormatException e){
			return false;
		}		
		PropertyHelper.writeToProperty(FILE_NAME, key, value);
//		String total = PropertyHelper.readFromProperty(FILE_NAME, "total");
//		if(total == null){
//			PropertyHelper.writeToProperty(FILE_NAME, "total", value);
//		}else{
//			float newTotal = Float.valueOf(total)+Float.valueOf(value);
//			PropertyHelper.writeToProperty(FILE_NAME, "total", String.valueOf(newTotal));
//		}
		return true;
	}
}
