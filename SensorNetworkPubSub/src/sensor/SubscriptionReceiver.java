package sensor;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SubscriptionReceiver extends Thread{

	//	private DatagramSocket socket;
	private SensorController controller;

	public SubscriptionReceiver(SensorController sensorController){
		this.controller = sensorController;
	}

	@Override
	public void run() {
		notifySubscribers();
		try {
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
