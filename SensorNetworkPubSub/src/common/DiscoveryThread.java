package common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Deprecated
public class DiscoveryThread implements Runnable{
	private DatagramSocket socket;
	private String receiveMsg;
	private String sendMsg;
	
	public DiscoveryThread(String receiveMsg, String sendMsg) {
		this.receiveMsg = receiveMsg;
		this.sendMsg = sendMsg;
	}

	@Override
	public void run() {
		try{
			socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while(true){
				System.out.println(getClass().getName() + " >> Ready to receive broadcast packets!");

				byte[] receiveBuffer = new byte[15000];
				DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				socket.receive(receivePacket);
				

				System.out.println(getClass().getName() + " >>Discovery packet received from: " + receivePacket.getAddress().getHostAddress());
				System.out.println(getClass().getName() + " >>Packet received; data: " + new String(receivePacket.getData()));

				String msg = new String(receivePacket.getData()).trim();
				if(msg.equals(receiveMsg)){
//					byte[] data = sendMsg.getBytes();
//					DatagramPacket sendPacket = new DatagramPacket(data, data.length, receivePacket.getAddress(), receivePacket.getPort());
//					socket.send(sendPacket);
//					System.out.println(getClass().getName()+ " >>Sent packet to: " + sendPacket.getAddress().getHostAddress());
					System.out.println(getClass().getName()+ " >>measurement received from: " + receivePacket.getAddress().getHostAddress());
				}
			}
		}catch(IOException e) {
			System.err.println("STOPPED!");
		}
	}
}
