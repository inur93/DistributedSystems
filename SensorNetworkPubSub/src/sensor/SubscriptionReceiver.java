package sensor;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class SubscriptionReceiver extends Thread{

	//	private DatagramSocket socket;
	private SensorController controller;

	public SubscriptionReceiver(SensorController sensorController){
		this.controller = sensorController;
	}

	@Override
	public void run() {

		
		try {
			notifySubscribers();
			receiveSubscriptions();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}
	private void receiveSubscriptions() throws IOException{
//		DatagramSocket socket = null;
		ServerSocket socket = null;
		try {
//			socket = new DatagramSocket(SensorController.RECEIVER_PORT, InetAddress.getByName("0.0.0.0"));// new DatagramSocket(SensorController.RECEIVER_PORT);
//			socket.setBroadcast(false);
			socket = new ServerSocket(SensorController.RECEIVER_PORT );
		} catch (SocketException e1) {

			controller.addMsgToLog(getClass().getSimpleName() + ">> creating socket failed");
		} catch (UnknownHostException e) {
			controller.addMsgToLog(getClass().getSimpleName() + ">> unknown host");
		}
		
		while(true){
		byte[] receiveBuffer = new byte[SensorController.PACKET_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
		DataInputStream dis = null;
		try {
			controller.addMsgToLog(getClass().getSimpleName() + ">> Ready to receive subscriptions: " + socket.getLocalSocketAddress());
			Socket clientSocket = socket.accept();
			controller.addMsgToLog(getClass().getSimpleName() + ">> client socket ready: " + clientSocket.getInetAddress());
			dis = new DataInputStream(clientSocket.getInputStream());
			controller.addMsgToLog(getClass().getSimpleName() + ">> input stream ready");
			Byte b = dis.readByte();
			controller.addMsgToLog("received " + b);
//			socket.receive(receivePacket);
		} catch (IOException e) {
			controller.addMsgToLog(getClass().getSimpleName() + ">> Receive packet failed - IOException: " + e.getStackTrace());
			break;
		}


		String msg = new String(receivePacket.getData()).trim();
		this.controller.addMsgToLog(getClass().getSimpleName() + ">> subscription received: " + msg);
		if(msg.equals(SensorController.TEMPERATURE_SUBSCRIBE_MSG)){

			this.controller.addSubscriber(receivePacket.getAddress());
			this.controller.addMsgToLog(getClass().getSimpleName() + ">> added subscriber: " + receivePacket.getAddress());
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
