package sensorServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import java.net.*;
import java.util.Enumeration;


public class Subscriber implements Runnable{
	private volatile DatagramSocket socket;
	private SensorServer sensorServer;
	private static final String DEFAULT_NAME = "255.255.255.255";
	private static final String PUBLISHER_DATA_MESSAGE = "DATA";
	private static final String SUBSCRIBE_MSG = "SUBSCRIBE_TEMPERATURE_SENSOR";
	private static final String SUBSCRIPTION_RECEIVED_MSG = "SUBSCRIPTION_RECEIVED";
	private static final int DEFAULT_PORT = 8888;
	private int PORT_NO = 0;
	public Subscriber(SensorServer sensorServer) {
		this.sensorServer = sensorServer;
	}

	@Override
	public void run() {
		int portOffset = 0;
		while(true){
			try {
				PORT_NO = DEFAULT_PORT + portOffset;
				this.socket = new DatagramSocket(PORT_NO);
				System.out.println(getClass().getName() + " >> socket on port: " + (PORT_NO));
				break;
			} catch (SocketException e) {
				portOffset++;
			}
		}
		//		socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
		notifyNodes();
		publisherHandler();
	}

	public void notifyNodes(){
		try{
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] data = SUBSCRIBE_MSG.getBytes();
			try{
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(DEFAULT_NAME), PORT_NO);
				socket.send(packet);
				System.out.println(getClass().getName() + " >> Request sent to: " + DEFAULT_NAME);
			}catch(Exception e){
				System.err.println("failed to send packet");
			}

			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements()){
				NetworkInterface networkInterface = interfaces.nextElement();

				if(networkInterface.isLoopback() || !networkInterface.isUp()){
					continue;
				}

				for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()){
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if(broadcast == null){
						continue;
					}

//					try{
						System.err.println("sending subscription: " + broadcast);
						sendSubscription(broadcast);
						System.err.println("done..");
//					}catch(Exception e){
//						System.err.println("failed to broadcast packet");
//					}
					System.out.println(getClass().getName() + " >> Request sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());

				}
			}

			System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");


		}catch(IOException e){}

	}

	public void publisherHandler(){
		int key = 0;
		try{

			this.socket.setBroadcast(true);
			this.socket.setSoTimeout(0);

			while(true){
				System.out.println(getClass().getName() + " >> Ready to receive broadcast packets!");

				byte[] receiveBuffer = new byte[15000];
				DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				socket.receive(receivePacket);


				System.out.println(getClass().getName() + " >>Discovery packet received from: " + receivePacket.getAddress().getHostAddress());
				System.out.println(getClass().getName() + " >>Packet received; data: " + new String(receivePacket.getData()));

				String[] rawMsg = new String(receivePacket.getData()).trim().split("_");
				String dataMsg = "";
				String dataVal = "";
				if(rawMsg.length > 1){
					dataMsg =rawMsg[0];
					dataVal = rawMsg[1];
				}
				if(dataMsg.equals(PUBLISHER_DATA_MESSAGE)){
					this.sensorServer.writeToProperty(String.valueOf(key), dataVal);
					key++;
					System.out.println(getClass().getName()+ " >>measurement received from: " + receivePacket.getAddress().getHostAddress());
					System.out.println(getClass().getName()+ " >>measurement value: " + dataVal);
				}
			}
		}catch(IOException e) {
			System.err.println("STOPPED!");
			e.printStackTrace();
		}

	}

	public void sendSubscription(InetAddress address){
		boolean subscriptionFailed = true;
		int tries = 0;
		byte[] data = SUBSCRIBE_MSG.getBytes();
		try {
			this.socket.setBroadcast(false);
			this.socket.setSoTimeout(1000);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		System.out.println(getClass().getName() + " >> sending subscription:" + subscriptionFailed);
		while(subscriptionFailed && tries < 10){
			System.out.println(getClass().getName() + " >>subscription try: " + tries);
			try{
				DatagramPacket packet = new DatagramPacket(data, data.length, address, DEFAULT_PORT);
				this.socket.send(packet);
				System.out.println(getClass().getName() + " >> request sent to: " + address);
			}catch(Exception e){
				System.err.println("failed to send packet");
			}

			DatagramPacket receivePacket = null;
			try{
//				this.socket = new DatagramSocket(DEFAULT_PORT+1);
				byte[] receiveBuffer = new byte[1500];
				receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				this.socket.receive(receivePacket);
			}catch(IOException e){
				subscriptionFailed = true;
			}
//			System.out.println(getClass().getName() + " >>packet: " + receivePacket.getSocketAddress());
//			System.out.println(getClass().getName() + " >>packet received from: " + receivePacket.getAddress().getHostAddress());
			System.out.println(getClass().getName() + " >>Packet received; data: " + new String(receivePacket.getData()));

			String msg = new String(receivePacket.getData()).trim();
			if(msg.equals(SUBSCRIPTION_RECEIVED_MSG)){
				subscriptionFailed = false;
				System.out.println(getClass().getName()+ " >>subscribed address: " + receivePacket.getAddress().getHostAddress());
			}else{
				subscriptionFailed = true;
			}

			tries++;
		}
	}

}
