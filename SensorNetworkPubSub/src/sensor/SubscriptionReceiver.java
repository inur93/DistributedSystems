package sensor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class SubscriptionReceiver extends Thread{

	//	private DatagramSocket socket;
	private SensorController controller;

	public SubscriptionReceiver(SensorController sensorController){
		this.controller = sensorController;
	}

	@Override
	public void run() {
		while(true){
		try {
			notifySubscribers();
		} catch (SocketException e) {
			System.err.println(getClass().getName() + ">> notify subscribers failed");
		}
		receiveSubscriptions();
		}

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
	private void notifySubscribers() throws SocketException{
		DatagramSocket socket = null;
		byte[] data = SensorController.TEMPERATURE_SENSOR_READY_MSG.getBytes();
		DatagramPacket packet = null;
		try{
			socket = new DatagramSocket();
			socket.setBroadcast(true);


			packet = new DatagramPacket(data, data.length, InetAddress.getByName(SensorController.DEFAULT_NAME), SensorController.PACKET_PORT);
			socket.send(packet);
			System.out.println(getClass().getName() + ">> broadcasting sensor is ready to: " + SensorController.DEFAULT_NAME);

		}catch(IOException e){
			System.err.println(getClass().getName() + ">> broadcast packet failed");
		}	
		
		  // Broadcast the message over all the network interfaces
		
		  Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		
		  while (interfaces.hasMoreElements()) {
		
		    NetworkInterface networkInterface = interfaces.nextElement();	 
		
		    if (networkInterface.isLoopback() || !networkInterface.isUp()) {
		
		      continue; // Don't want to broadcast to the loopback interface
		
		    }
		
		    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
		
		      InetAddress broadcast = interfaceAddress.getBroadcast();
		
		      if (broadcast == null) {
		        continue;
		      }
		
		      // Send the broadcast package!
		
		      try {
		
		        socket.send(packet);
		
		      } catch (IOException e) {
		    	  System.err.println(getClass().getName() + ">> failed to send packet");
		      }
	
		      System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
		
		    }
	
		  }

	}

}
