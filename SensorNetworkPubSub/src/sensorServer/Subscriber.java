package sensorServer;

import java.io.IOException;
import java.net.*;


public class Subscriber implements Runnable{

	public Subscriber() {

	}
	@Override
	public void run() {
		notifyNodes();
	}

	public void notifyNodes(){
		try{
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] data = SensorServer.SUBSCRIBE_MSG.getBytes();
			try{
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(SensorServer.DEFAULT_NAME), SensorServer.PACKET_PORT);
				socket.send(packet);
				System.out.println(getClass().getName() + ">> Request sent to: " + SensorServer.DEFAULT_NAME);
			}catch(IOException e){
				System.err.println(getClass().getName() + ">> failed to send packet");
			}
			socket.close();
		}catch(SocketException e){}
		
	}

	public void sendSubscription(InetAddress address){
		boolean subscriptionFailed = true;
		DatagramSocket socket = null;
		int tries = 0;
		byte[] data = SensorServer.SUBSCRIBE_MSG.getBytes();
		try {
			socket = new DatagramSocket(SensorServer.SUBSCRIPTION_PORT);
			socket.setBroadcast(true);
			socket.setSoTimeout(0);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		while(subscriptionFailed && tries < 10){
			System.out.println(getClass().getName() + ">> subscription try: " + tries);
			try{
				DatagramPacket packet = new DatagramPacket(data, data.length, address, SensorServer.PACKET_PORT);
				socket.send(packet);
				System.out.println(getClass().getName() + ">> subscription sent to: " + address);
			}catch(Exception e){
				System.err.println(getClass().getName() + ">> failed to send subscription packet");
			}

			DatagramPacket receivePacket = null;
			try{
				byte[] receiveBuffer = new byte[SensorServer.PACKET_SIZE];
				System.out.println(getClass().getName() + ">> waiting for ack on port: " + socket.getLocalSocketAddress());
				receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				socket.receive(receivePacket);
			}catch(IOException e){
				System.err.println(getClass().getName() + ">> subscription failed.. trying again...");
				subscriptionFailed = true;
			}
			System.out.println(getClass().getName() + ">> Packet received; data: " + new String(receivePacket.getData()));

			String msg = new String(receivePacket.getData()).trim();
			if(msg.equals(SensorServer.SUBSCRIPTION_RECEIVED_MSG)){
				subscriptionFailed = false;
				System.out.println(getClass().getName()+ ">> subscribed address: " + receivePacket.getAddress().getHostAddress());
			}else{
				subscriptionFailed = true;
			}

			tries++;
		}
	}

}
