package sensor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SubscriptionReceiver extends Thread{
	private static final String DEFAULT_NAME = "255.255.255.255";
	private static final int DEFAULT_PORT = 8888;
	public static final String FILE_NAME = "temperature";
	public static final String SENSOR_READY_MSG = "NEW_TEMPERATURE_SENSOR_READY";
	public static final String SUBSCRIBER_MSG = "SUBSCRIBE";
	public static final String SUBSCRIPTION_ACCEPTED_MSG = "SUBSCRIPTION_ACCEPTED";
	public static final String DATA_MSG_HEADER = "DATA";
	public static final int PACKET_SIZE = 512;
	private DatagramSocket socket;
	private Publisher publisher;
	public SubscriptionReceiver(DatagramSocket socket, Publisher publisher){
		this.socket = socket;
		this.publisher = publisher;
	}

	@Override
	public void run() {
		int portOffset = 0;
		boolean sendFailed = false;
		byte[] data = SUBSCRIPTION_ACCEPTED_MSG.getBytes();

		while(true){
			byte[] receiveBuffer = new byte[PACKET_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			try {
				System.err.println(getClass().getName() + ">> Ready to receive subscriptions");
				socket.receive(receivePacket);
			} catch (IOException e) {
				System.err.println(getClass().getName() + ">> Receive packet failed - IOException: " + e.getStackTrace());
			}

			System.out.println(getClass().getName() + ">> Broadcast response from subscriber: " + receivePacket.getAddress().getHostAddress());

			String msg = new String(receivePacket.getData()).trim();
			if(msg.equals(SUBSCRIBER_MSG)){
				if(!this.publisher.subscribers.contains(receivePacket.getAddress())){
					System.out.println("socket address: " + receivePacket.getSocketAddress());
					this.publisher.subscribers.add(receivePacket.getAddress());

					while(sendFailed){
						try {
							System.out.println(getClass().getName() + ">> sending subscription ack");
							DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(DEFAULT_NAME), DEFAULT_PORT+portOffset);
							socket.send(sendPacket);
							sendFailed = false;
						} catch (IOException e) {
							portOffset++;
							sendFailed = true;
							System.err.println("send failed");
						}
					}

				}
			}
		}
	}

}
