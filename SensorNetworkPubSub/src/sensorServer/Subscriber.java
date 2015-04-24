package sensorServer;

import java.io.IOException;
import java.net.*;

import sensorServer.gui.ServerGUI;
import common.Constants;


public class Subscriber implements Runnable{

	private ServerGUI log;
	private String topic;
	public Subscriber(String topic, ServerGUI log) {
		this.topic = topic;
		this.log = log;
	}
	@Override
	public void run() {
	}


	public void subscribe(InetAddress address){	
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			socket = new DatagramSocket();

		} catch (IOException e) {
			this.log.addMsg(getClass().getSimpleName() + ">> failed to instantiate new socket");
			System.err.println(e.getLocalizedMessage());
			return;
		}

		byte[] data = (this.topic + Constants.SUBSCRIBE_EVENT) .getBytes();
		try {
			packet = new DatagramPacket(data, data.length, address, Constants.PUBLISHER_PORT); // InetAddress.getByName("10.16.175.255 

			socket.send(packet);

			this.log.addMsg(getClass().getSimpleName() + ">> sent subscribe event to: " + packet.getSocketAddress() + " packet data: "+ new String(packet.getData()));
			socket.close();
		} catch (IOException e) {
			System.err.println("write socket error");
		} 
	}

}
