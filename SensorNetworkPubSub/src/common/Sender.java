package common;

import java.io.IOException;
import java.net.*;



public class Sender{

	private ILog log;
	private DatagramSocket socket;
	public Sender(DatagramSocket socket, ILog log) {
		this.socket = socket;
		this.log = log;
	}

	public void send(Event event){	
		InetAddress address = event.address;
		String eventStr = event.getEventStr();
		DatagramPacket packet = null;
		try {
			socket.setBroadcast(event.broadcast);
		} catch (IOException e) {
			this.log.addMsg(getClass().getSimpleName() + ">> failed to set broadcast on socket");
			System.exit(0);
			return;
		}

		byte[] data = eventStr.getBytes();
		try {
			packet = new DatagramPacket(data, data.length, address, event.destPort);

			socket.send(packet);

			if(event.value.equals(Constants.SUBSCRIBE_VALUE) || event.value.equals(Constants.READY_VALUE)){
				this.log.addMsg(getClass().getSimpleName() + ">> sent event to: " + packet.getSocketAddress() + " data: "+ new String(packet.getData()));
			}
//			socket.close();
		} catch (IOException e) {
			this.log.addMsg("write socket error");
		} 
	}

}
