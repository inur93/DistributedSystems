package common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import sensorServer.IController;


public class Receiver implements Runnable{

	private volatile boolean terminate = false;
	private IController controller;
	private DatagramSocket socket;
	private ILog log;
	public Receiver(IController controller, ILog log, DatagramSocket socket) {
		this.controller = controller;
		this.socket = socket;
		this.log = log;

	}

	@Override
	public void run() {



		try {
			socket.setBroadcast(true);
		} catch (SocketException e1) {
			this.log.addMsg(getClass().getSimpleName() + ">> setBroadcast failed");
		}
		while(!this.terminate){
			DatagramPacket receivePacket = null;
			try{
				byte[] receiveBuffer = new byte[Constants.PACKET_SIZE];
				receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				this.socket.receive(receivePacket);
			} catch (IOException e){
				this.log.addMsg(getClass().getSimpleName() + ">> ioexception");
				break;
			}

			String dataMsg = new String(receivePacket.getData()).trim();
			this.log.addMsg(getClass().getSimpleName() + ">> data received: " + dataMsg);

			
			dataMsg = dataMsg.replace(",", ".");
			String[] eventStr = dataMsg.split(";");
			String topic = "";
			String value = "";
			try{
				topic = eventStr[0];
				value = eventStr[1];
			}catch(IndexOutOfBoundsException e){
				log.addMsg(getClass() + ">> invalid event received: " + dataMsg);
			}
			
			
			
			Event event = new Event(topic, value, receivePacket.getAddress(), receivePacket.getPort());
			controller.receiveEvent(event);	
		}
	}
	
	public synchronized void terminate(){
		this.terminate = true;
	}	

}
