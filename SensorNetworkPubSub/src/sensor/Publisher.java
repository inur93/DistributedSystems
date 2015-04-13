package sensor;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;

public class Publisher extends Thread{

	private SensorController controller;
	public Publisher(SensorController controller) {
		this.controller = controller;
	}


	public void publish(String msg){
		byte[] data = (SensorController.TEMPERATURE_SENSOR_DATA_VAL + msg).getBytes();
		LinkedList<InetAddress> addresses = this.controller.getSubscribers();
		if(!addresses.isEmpty()){
			for(InetAddress address : addresses){
				try{
					DatagramSocket senderSocket = new DatagramSocket();
					System.err.println(getClass().getName() + " >> sending data: " + msg + " to: " + address.toString());
					DatagramPacket packet = new DatagramPacket(data, data.length, address, SensorController.PACKET_PORT);
					senderSocket.send(packet);
					senderSocket.close();
				}catch(Exception e){
					e.printStackTrace();
					System.err.println("failed to send packet to: " + address.toString());
				}
			}
		}
	}
}
