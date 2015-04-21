package sensorServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class DataHandler implements Runnable{

	private SensorServerController controller;
	private DatagramSocket socket;
	public DataHandler(SensorServerController sensorServer, DatagramSocket socket) {
		this.controller = sensorServer;
		this.socket = socket;

	}

	@Override
	public void run() {

		int key = 0;

		try {
			socket.setBroadcast(true);
		} catch (SocketException e1) {
			controller.writeToLog(getClass().getSimpleName() + ">> setBroadcast failed");
		}
		while(!this.controller.terminate){
			DatagramPacket receivePacket = null;
			try{
				byte[] receiveBuffer = new byte[SensorServerController.PACKET_SIZE];
				receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				socket.receive(receivePacket);
			} catch (IOException e){
				controller.writeToLog(getClass().getSimpleName() + ">> ioexception");
				break;
			}

			String dataMsg = new String(receivePacket.getData()).trim();

			dataMsg = dataMsg.replace(",", ".");
			String topic = dataMsg.split(";")[0];
			String value = dataMsg.split(";")[1];
			controller.writeToLog(getClass().getSimpleName() + ">> data in: " + dataMsg);


			if(topic.contains(SensorServerController.TEMP_TOPIC.replace(";", ""))){
				if(value.matches("\\d{2}.\\d{2}")){

					dataMsg = dataMsg.replace(SensorServerController.TEMP_TOPIC.replace(";", ""), "");
					this.controller.writeToProperty(String.valueOf(key), dataMsg);
					key++;

				}else if(value.contains(SensorServerController.TEMP_READY.replace(";", ""))){
					controller.writeToLog(getClass().getSimpleName() + " >> Sending subscription to: " + receivePacket.getAddress());
					controller.sendSubscription(receivePacket.getAddress());
				}
			}
		}


	}

	

}
