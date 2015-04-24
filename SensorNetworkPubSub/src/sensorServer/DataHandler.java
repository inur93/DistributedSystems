package sensorServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import sensorServer.gui.ServerGUI;
import common.Constants;
import common.PropertyHelper;


public class DataHandler implements Runnable{

	private volatile boolean terminate = false;
	private Subscriber subscriber;
	private DatagramSocket socket;
	private ServerGUI log;
	private String topic;
	public DataHandler(Subscriber subscriber, ServerGUI log, DatagramSocket socket, String topic) {
		this.subscriber = subscriber;
		this.socket = socket;
		this.topic = topic;
		this.log = log;

	}

	@Override
	public void run() {

		int key = PropertyHelper.findLastIndex();

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
			String topic = "";
			String value = "";
			try{
			topic = dataMsg.split(";")[0];
			value = dataMsg.split(";")[1];
			}catch(IndexOutOfBoundsException e){
				this.log.addMsg(getClass().getSimpleName() + ">> ignoring data");
			}
			this.log.addMsg(getClass().getSimpleName() + ">> data received: " + dataMsg);


			if(topic.contains(this.topic.replace(";", ""))){
				if(value.matches("\\d{2}.\\d{2}")){

				
					writeToProperty(String.valueOf(key), value.replace(";", ""));
					key++;

				}else if(value.contains(Constants.READY_EVENT.replace(";", ""))){
					this.log.addMsg(getClass().getSimpleName() + " >> Sending subscription to: " + receivePacket.getAddress());
					this.subscriber.subscribe(receivePacket.getAddress());
				}
			}
		}


	}
	
	public synchronized void terminate(){
		this.terminate = true;
	}

	/**
	 * verifies input and write data to file
	 * @param key should be integer so the index can be used for calculating total and mean
	 * @param val char[] that will be verified, can have the form (\d+((.|,)(\d+))?)
	 * @return if not able to convert to float false will be returned else data will be saved and true returned
	 */
	public boolean writeToProperty(String key, String value){
		value = value.replace(',', '.');
		try{
			Float.valueOf(value);
		}catch(NumberFormatException e){
			return false;
		}		
		PropertyHelper.writeToProperty(Constants.FILE_NAME, key, value);
		return true;
	}
	

}
