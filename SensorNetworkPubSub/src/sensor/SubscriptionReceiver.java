package sensor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class SubscriptionReceiver extends Thread{

	//	private DatagramSocket socket;
	private SensorController controller;

	public SubscriptionReceiver(SensorController sensorController){
		this.controller = sensorController;
	}

	@Override
	public void run() {
		notifySubscribers();
		receiveSubscriptions();

	}
	private void receiveSubscriptions(){
		byte[] data = SensorController.SUBSCRIPTION_ACCEPTED_MSG.getBytes();
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(SensorController.RECEIVER_PORT);
			socket.setBroadcast(false);
		} catch (SocketException e1) {
			System.err.println(getClass().getName() + ">> creating socket failed");
		}
		while(true){
			byte[] receiveBuffer = new byte[SensorController.PACKET_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			try {
				System.err.println(getClass().getName() + ">> Ready to receive subscriptions");
				socket.receive(receivePacket);
			} catch (IOException e) {
				System.err.println(getClass().getName() + ">> Receive packet failed - IOException: " + e.getStackTrace());
			}


			String msg = new String(receivePacket.getData()).trim();
			if(msg.equals(SensorController.TEMPERATURE_SUBSCRIBE_MSG)){

				this.controller.addSubscriber(receivePacket.getAddress());
				try {
					DatagramPacket sendPacket = new DatagramPacket(data, data.length, receivePacket.getAddress(), 1234);
					System.out.println(getClass().getName() + ">> ack packet to: " + sendPacket.getAddress());
					socket.send(sendPacket);
				} catch (IOException e) {
					System.err.println(getClass().getName() + ">> ack packet failed send to: " + receivePacket.getAddress());
					break;
				}


			}
		}
		if(socket != null) socket.close();
	}
	private void notifySubscribers(){
		DatagramSocket socket = null;
		try{
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] data = SensorController.TEMPERATURE_SENSOR_READY_MSG.getBytes();

			DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(SensorController.DEFAULT_NAME), SensorController.PACKET_PORT);
			socket.send(packet);
			System.out.println(getClass().getName() + ">> broadcasting sensor is ready to: " + SensorController.DEFAULT_NAME);

		}catch(IOException e){
			System.err.println(getClass().getName() + ">> broadcast packet failed");
		}
	}

}
