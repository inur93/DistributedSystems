package sensorServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class DataHandler implements Runnable{

	private SensorServerController controller;
	public DataHandler(SensorServerController sensorServer) {
		this.controller = sensorServer;
	
	}

	@Override
	public void run() {

		try {
			receiveData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void receiveData() throws IOException{

		DatagramSocket socket = new DatagramSocket(SensorServerController.RECEIVER_PORT, InetAddress.getByName("0.0.0.0"));

		int key = 0;

		socket.setSoTimeout(0);
		socket.setBroadcast(true);
		while(true){
			DatagramPacket receivePacket = null;
			try{
			byte[] receiveBuffer = new byte[SensorServerController.PACKET_SIZE];
			receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			socket.receive(receivePacket);
			} catch (IOException e){
				break;
			}

			String dataMsg = new String(receivePacket.getData()).trim();

			dataMsg = dataMsg.replace(",", ".");

			controller.writeToLog(getClass().getSimpleName() + ">> data in: " + dataMsg);

			if(dataMsg.contains(SensorServerController.TEMPERATURE_SENSOR_DATA_VAL)){

				dataMsg = dataMsg.replace(SensorServerController.TEMPERATURE_SENSOR_DATA_VAL, "");
				dataMsg = dataMsg.replace("_", "");
				this.controller.writeToProperty(String.valueOf(key), dataMsg);
				key++;

			}else if(dataMsg.equals(SensorServerController.TEMPERATURE_SENSOR_READY_MSG)){
				controller.writeToLog(getClass().getSimpleName() + " >> Sending subscription to: " + receivePacket.getAddress());
				controller.sendSubscription(receivePacket.getAddress());
			}
		}
		if(socket != null) socket.close();

	}

}
