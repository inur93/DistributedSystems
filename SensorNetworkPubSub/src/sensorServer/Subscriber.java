package sensorServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;


public class Subscriber implements Runnable{

	private SensorServerController controller;
	public Subscriber(SensorServerController controller) {
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
			byte[] data = SensorServerController.SUBSCRIBE_MSG.getBytes();
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
		Socket socket = null;
		
		try {
			socket = new Socket(address, 8889);// SensorServer.SUBSCRIPTION_PORT);
		} catch (IOException e) {
			System.err.println("socket create error");
		}
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("dos create error");
		}
		byte[] data = SensorServerController.SUBSCRIBE_MSG.getBytes();
		try {
			dos.write(data);
			this.controller.writeToLog(getClass().getSimpleName() + ">> sent subscription: " + socket.getRemoteSocketAddress());
			socket.setSoTimeout(0);
		} catch (IOException e) {
			System.err.println("write socket error");
		}
	}

}
