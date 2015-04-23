package sensorServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import common.Constants;
import common.PropertyHelper;


public class DataHandler implements Runnable{

	private SensorServerController controller;
	private DatagramSocket socket;
	private String topic;
	public DataHandler(SensorServerController sensorServer, DatagramSocket socket, String topic) {
		this.controller = sensorServer;
		this.socket = socket;
		this.topic = topic;

	}

	@Override
	public void run() {

		int key = PropertyHelper.findLastIndex();

		try {
			socket.setBroadcast(true);
		} catch (SocketException e1) {
			controller.writeToLog(getClass().getSimpleName() + ">> setBroadcast failed");
		}
		while(!this.controller.terminate){
			DatagramPacket receivePacket = null;
			try{
				byte[] receiveBuffer = new byte[Constants.PACKET_SIZE];
				receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				socket.receive(receivePacket);
			} catch (IOException e){
				controller.writeToLog(getClass().getSimpleName() + ">> ioexception");
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
				controller.writeToLog(getClass().getSimpleName() + ">> ignoring data");
			}
			controller.writeToLog(getClass().getSimpleName() + ">> data received: " + dataMsg);


			if(topic.contains(this.topic.replace(";", ""))){
				if(value.matches("\\d{2}.\\d{2}")){

				
					this.controller.writeToProperty(String.valueOf(key), value.replace(";", ""));
					key++;

				}else if(value.contains(Constants.READY_EVENT.replace(";", ""))){
					controller.writeToLog(getClass().getSimpleName() + " >> Sending subscription to: " + receivePacket.getAddress());
					controller.sendSubscription(receivePacket.getAddress());
				}
			}
		}


	}

	

}
