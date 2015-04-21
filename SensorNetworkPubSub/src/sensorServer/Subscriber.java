package sensorServer;

import java.io.IOException;
import java.net.*;


public class Subscriber implements Runnable{

	private SensorServerController controller;
	private String topic;
	public Subscriber(String topic, SensorServerController controller) {
		this.topic = topic;
		this.controller = controller;
	}
	@Override
	public void run() {
		notifyNodes();
	}

	public void notifyNodes(){
		try{
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] data = (this.topic + "SUBSCRIBE;").getBytes();
			try{
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(SensorServerController.DEFAULT_NAME), SensorServerController.PACKET_PORT);
				socket.send(packet);
			
				controller.writeToLog(getClass().getSimpleName() + ">> Request sent to: " + SensorServerController.DEFAULT_NAME);
			}catch(IOException e){
				controller.writeToLog(getClass().getSimpleName() + ">> failed to send packet");
			}
			socket.close();
		}catch(SocketException e){}
		
	}

	public void sendSubscription(InetAddress address){		
//		Socket socket = null;
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			socket = new DatagramSocket(); // SensorServerController.SUBSCRIPTION_PORT
		
		} catch (IOException e) {
			controller.writeToLog(getClass().getSimpleName() + ">> failed to instantiate new socket");
			System.err.println(e.getLocalizedMessage());
			return;
		}

		byte[] data = (this.topic + "SUBSCRIBE;") .getBytes();
		try {
//			Thread.sleep(3000);
			packet = new DatagramPacket(data, data.length, address, SensorServerController.PACKET_PORT); // InetAddress.getByName("10.16.175.255")

			socket.send(packet);
			this.controller.writeToLog(getClass().getSimpleName() + ">> packet data: " + new String(packet.getData()));

			this.controller.writeToLog(getClass().getSimpleName() + ">> sent subscription: " + packet.getSocketAddress());
			socket.close();
		} catch (IOException e) {
			System.err.println("write socket error");
		} 
//		catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
