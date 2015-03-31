package sensorServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DiscoveryThread implements Runnable{
	private DatagramSocket socket;
	public DiscoveryThread() {
		// TODO Auto-generated constructor stub
	}
	
	public static DiscoveryThread getInstance(){
		return DiscoveryThreadHolder.INSTANCE;
	}

	@Override
	public void run() {
		try{
			socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			
			while(true){
				System.out.println(getClass().getName() + " >> Ready to receive broadcast packets!");
				
				byte[] receiveBuffer = new byte[15000];
				DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				socket.receive(packet);
				
				System.out.println(getClass().getName() + " >>Discovery packet received from: " + packet.getAddress().getHostAddress());
				System.out.println(getClass().getName() + " >>Packet received; data: " + new String(packet.getData()));
				
				String msg = new String(packet.getData()).trim();
				if(msg.equals("TEST_SEND")){
					byte[] data = "TEST_RECEIVE".getBytes();
				
				
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
				socket.send(sendPacket);
				System.out.println(getClass().getName()+ " >>Sent packet to: " + sendPacket.getAddress().getHostAddress());
				}
			}
		}catch(IOException e) {
			
		}
	}

	private static class DiscoveryThreadHolder{
		private static final DiscoveryThread INSTANCE = new DiscoveryThread();
	}
}
