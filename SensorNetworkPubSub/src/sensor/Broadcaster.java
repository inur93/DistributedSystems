package sensor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Broadcaster implements Runnable{

	private SensorController controller;

	public Broadcaster(SensorController controller){
		this.controller = controller;
	}
	@Override
	public void run() {
		DatagramSocket socket = null;
		byte[] data = SensorController.BROADCAST_EVENT.getBytes();
		DatagramPacket packet = null;
		try{
			socket = new DatagramSocket();
			socket.setBroadcast(true);


			packet = new DatagramPacket(data, data.length, InetAddress.getByName(SensorController.DEFAULT_NAME), SensorController.PACKET_PORT);
//			socket.send(packet);
//			controller.addMsgToLog(getClass().getSimpleName() + ">> data sent: " + new String(packet.getData()));

		}catch(IOException e){
			System.err.println(getClass().getName() + ">> broadcast packet failed");
		}	
		Enumeration<NetworkInterface> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
	
	
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
						controller.addMsgToLog(getClass().getSimpleName() + ">> sent data: " + new String(packet.getData()));
					} catch (IOException e) {
						System.err.println(getClass().getName() + ">> failed to send packet");
					}
	
					System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
	
				}
	
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
