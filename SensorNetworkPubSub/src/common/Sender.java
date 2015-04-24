package common;

import java.io.IOException;
import java.net.*;



public class Sender implements Runnable{

	private ILog log;
	public Sender(ILog log) {
		this.log = log;
	}
	@Override
	public void run() {
	}


	public void send(Event event){	
		InetAddress address = event.address;
		String eventStr = event.getEventStr();
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(event.broadcast);
		} catch (IOException e) {
			this.log.addMsg(getClass().getSimpleName() + ">> failed to instantiate new socket");
			System.err.println(e.getLocalizedMessage());
			return;
		}

		byte[] data = eventStr.getBytes();
		try {
			packet = new DatagramPacket(data, data.length, address, event.port); // InetAddress.getByName("10.16.175.255 

			socket.send(packet);

			this.log.addMsg(getClass().getSimpleName() + ">> sent subscribe event to: " + packet.getSocketAddress() + " packet data: "+ new String(packet.getData()));
			socket.close();
		} catch (IOException e) {
			System.err.println("write socket error");
		} 
	}

}
