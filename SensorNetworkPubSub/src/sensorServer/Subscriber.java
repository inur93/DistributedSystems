package sensorServer;

import java.io.IOException;
import java.net.*;

import common.Constants;


public class Subscriber implements Runnable{

	private SensorServerController controller;
	private String topic;
	public Subscriber(String topic, SensorServerController controller) {
		this.topic = topic;
		this.controller = controller;
	}
	@Override
	public void run() {
//		broadcast(topic + SensorServerController.SUBSCRIBE_EVENT);
	}

//	public void broadcast(String event){
//		try{
//			DatagramSocket socket = new DatagramSocket();
//			socket.setBroadcast(true);
//			byte[] data = (event).getBytes();
//			try{
//				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(SensorServerController.DEFAULT_NAME), SensorServerController.PACKET_PORT);
//				socket.send(packet);
//				controller.writeToLog(getClass().getSimpleName() + ">> Request sent to: " + SensorServerController.DEFAULT_NAME);
//			}catch(IOException e){
//				controller.writeToLog(getClass().getSimpleName() + ">> failed to send packet");
//			}
//
//			socket.close();
//		}catch(SocketException e){}
//
//	}

	public void subscribe(InetAddress address){	
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			socket = new DatagramSocket();

		} catch (IOException e) {
			controller.writeToLog(getClass().getSimpleName() + ">> failed to instantiate new socket");
			System.err.println(e.getLocalizedMessage());
			return;
		}

		byte[] data = (this.topic + Constants.SUBSCRIBE_EVENT) .getBytes();
		try {
			packet = new DatagramPacket(data, data.length, address, Constants.PUBLISHER_PORT); // InetAddress.getByName("10.16.175.255 

			socket.send(packet);

			this.controller.writeToLog(getClass().getSimpleName() + ">> sent subscribe event to: " + packet.getSocketAddress() + " packet data: "+ new String(packet.getData()));
			socket.close();
		} catch (IOException e) {
			System.err.println("write socket error");
		} 
	}

}
