package sensorServer;
import java.net.*;
import java.util.Enumeration;
import java.io.*;

import sensor.Publisher;
import common.DiscoveryThread;
import common.PropertyHelper;


public class SensorServer implements Runnable{
	private DatagramSocket socket;
	private static final String DEFAULT_NAME = "255.255.255.255";
	private static final int DEFAULT_PORT = 8888;
	
	public static final String FILE_NAME = "temperature";
	public static final int DATA_SIZE = 5;
	
	public static void main(String[] args){ 
//		DiscoveryThread dt = new DiscoveryThread("TEMPERATURE_SENSOR_READY", "SUBSCRIBE");
//		dt.run();
		SensorServer server = new SensorServer();
		Subscriber subscriber = new Subscriber(server);
		new Thread(subscriber).start();
//		SensorServer s = new SensorServer();
//		s.run();
	}

	public void run(){
		Subscriber subscriber = new Subscriber(this);
		new Thread(subscriber).start();
		
		DataHandler dataHandler = new DataHandler(this);
		new Thread(dataHandler).start();
//		try{
//		socket = new DatagramSocket();
//		socket.setBroadcast(true);
//		byte[] data = "TEST_SENDING".getBytes();
//		try{
//			DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(DEFAULT_NAME), DEFAULT_PORT);
//			socket.send(packet);
//			System.out.println(getClass().getName() + " >> Request sent to: " + DEFAULT_NAME);
//		}catch(Exception e){
//			System.err.println("failed to send packet");
//		}
//		
//		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
//		while(interfaces.hasMoreElements()){
//			NetworkInterface networkInterface = interfaces.nextElement();
//			
//			if(networkInterface.isLoopback() || !networkInterface.isUp()){
//				continue;
//			}
//			
//			for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()){
//				InetAddress broadcast = interfaceAddress.getBroadcast();
//				if(broadcast == null){
//					continue;
//				}
//				
//				try{
//					DatagramPacket packet = new DatagramPacket(data, data.length, broadcast, DEFAULT_PORT);
//					socket.send(packet);
//				}catch(Exception e){
//					System.err.println("failed to broadcast packet");
//				}
//				
//				System.out.println(getClass().getName() + " >> Request sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
//				
//			}
//		}
//		
//		System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");
//
//		byte[] receiveBuffer = new byte[15000];
//		DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
//		socket.receive(receivePacket);
//		
//		System.out.println(getClass().getName() + ">> Broadcast response from subscriber: " + receivePacket.getAddress().getHostAddress());
//		
//		String msg = new String(receivePacket.getData()).trim();
//		if(msg.equals("TEST_RECEIVE")){
//			System.out.println("server ip address to store: " + receivePacket.getAddress());
//		}
//		
//		socket.close();
//	}catch(IOException e){
//		
//	}
	}

	/**
	 * verifies input and write data to file
	 * @param key should be integer so the index can be used for calculating total and mean
	 * @param val char[] that will be verified, can have the form (\d+((.|,)(\d+))?)
	 * @return if not able to convert to float false will be returned else data will be saved and true returned
	 */
	public boolean writeToProperty(String key, String value){
//		String value = "";
//		for(int i = 0; i<val.length;i++){
//			if(val[i] != '\u0000'){
//				value = value + val[i];
//			}
//		}
		value = value.replace(',', '.');
		try{
			Float.valueOf(value);
		}catch(NumberFormatException e){
			return false;
		}		
		PropertyHelper.writeToProperty(FILE_NAME, key, value);
		return true;
	}
}
