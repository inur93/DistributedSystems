package deprecated;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;

import sensor.Publisher;
import common.Constants;

public class PublisherOld extends Thread{

	private Publisher controller;
	private DatagramSocket socket;
	public PublisherOld(Publisher controller, DatagramSocket socket) {
		this.controller = controller;
		this.socket = socket;
	}


	public void publish(String event){
		byte[] data = (event).getBytes();
		LinkedList<InetAddress> addresses = this.controller.getSubscribers();
		if(!addresses.isEmpty()){
			for(InetAddress address : addresses){
				try{
//					DatagramPacket packet = new DatagramPacket(data, data.length, address, Constants.SUBSCRBER_PORT);
//					socket.send(packet);
				}catch(Exception e){
					e.printStackTrace();
					System.err.println("failed to send packet to: " + address.toString());
				}
			}
		}
	}
}
