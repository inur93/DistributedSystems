package common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 
 * @author Runi
 *
 */
public class Broadcaster implements Runnable{
	private String event;
	private int destinationPort;
	private ILog log;
	public Broadcaster(String event, int destinationPort, ILog log){
		this.event = event;
		this.destinationPort = destinationPort;
		this.log = log;
	}
	@Override
	public void run() {
		DatagramSocket socket = null;
		byte[] data = (this.event).getBytes();
		DatagramPacket packet = null;
		try{
			socket = new DatagramSocket();
			socket.setBroadcast(true);

			packet = new DatagramPacket(data, data.length, InetAddress.getByName(Constants.DEFAULT_NAME), destinationPort);
			socket.send(packet);
			this.log.addMsg(getClass().getSimpleName() + "broadcast data: " + new String(packet.getData()));
		}catch(IOException e){
			this.log.addMsg(getClass().getSimpleName() + ">> broadcast packet failed");
		}	
	}
}
