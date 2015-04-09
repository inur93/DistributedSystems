package sensor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;

public class Publisher extends Thread{
	private volatile DatagramSocket socket;
	private static final String DEFAULT_NAME = "255.255.255.255";
	private static final int DEFAULT_PORT = 8888;

	public static final String FILE_NAME = "temperature";
	public static final String SENSOR_READY_MSG = "NEW_TEMPERATURE_SENSOR_READY";
	public static final String SUBSCRIBER_MSG = "SUBSCRIBE";
	public static final String SUBSCRIPTION_ACCEPTED_MSG = "SUBSCRIPTION_ACCEPTED";
	public static final String DATA_MSG_HEADER = "DATA";
	public static final int DATA_SIZE = 5;
	public volatile LinkedList<InetAddress> subscribers = new LinkedList<InetAddress>();
	public Publisher() {
		// TODO Auto-generated constructor stub
	}

	private void notifySubscribers(){
		try{
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] data = SENSOR_READY_MSG.getBytes();
			try{
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(DEFAULT_NAME), DEFAULT_PORT);
				socket.send(packet);
				System.out.println(getClass().getName() + " >> request sent to: " + DEFAULT_NAME);
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

					try{
						DatagramPacket packet = new DatagramPacket(data, data.length, broadcast, DEFAULT_PORT);
						socket.send(packet);
					}catch(Exception e){
						System.err.println("failed to broadcast packet");
					}

					System.out.println(getClass().getName() + " >> Request sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());

				}
			}
			System.out.println(getClass().getName() + ">> Done looping over all network interfaces. Now waiting for a reply!");


		}catch(IOException e){

		}
	}

	public void publish(String msg){
		byte[] data = (DATA_MSG_HEADER + msg).getBytes();
		for(InetAddress address : subscribers){
			try{
				System.err.println(getClass().getName() + " >> sending data: " + msg + " to: " + address.getAddress());
				DatagramPacket packet = new DatagramPacket(data, data.length, address, DEFAULT_PORT);
				socket.send(packet);
			}catch(Exception e){
				System.err.println("failed to send packet to: " + address.getAddress());
			}
		}
	}

	public void run(){
		synchronized (this) {	
		notifySubscribers();
		SubscriptionReceiver sr = new SubscriptionReceiver(this.socket, this);
		Thread subscriptionHandler = new Thread(sr);
		subscriptionHandler.start();
		this.notify();
		}
	}
}