package common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import sensorServer.IController;

/**
 * 
 * @author Runi
 *	Simple receiver class taking a IController as argument to be able to return data.
 */
public class Receiver implements Runnable{

	private volatile boolean terminate = false; // stops while loop if true
	private IController controller;
	private DatagramSocket socket;
	private ILog log;
	/**
	 * 
	 * @param controller to which data is returned
	 * @param log for easier overview and debugging
	 * @param socket
	 */
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
			

			
			dataMsg = dataMsg.replace(",", ".");
			String[] eventStr = dataMsg.split(";");
			String topic = "";
			String value = "";
			int port = 0;
			try{
				topic = eventStr[0];
				value = eventStr[1];
				port = Integer.valueOf(eventStr[2]);
			}catch(IndexOutOfBoundsException e){
				log.addMsg(getClass() + ">> invalid event received: " + dataMsg);
			}catch(NumberFormatException e1){
				port = 0;
			}
			if(value.equals(Constants.SUBSCRIBE_VALUE) || value.equals(Constants.READY_VALUE)){
				this.log.addMsg(getClass().getSimpleName() + ">> data received: " + dataMsg);
			}
			Event event = new Event(new Topic(topic, port), value, receivePacket.getAddress(), receivePacket.getPort());
			controller.receiveEvent(event);	
		}
	}
	
	public synchronized void terminate(){
		this.terminate = true;
	}	

}
