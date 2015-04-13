package sensorServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class DataHandler implements Runnable{

	private SensorServer sensorServer;

	public DataHandler(SensorServer sensorServer) {
		this.sensorServer = sensorServer;
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

		DatagramSocket socket = new DatagramSocket(SensorServer.RECEIVER_PORT, InetAddress.getByName("0.0.0.0"));

		int key = 0;

		socket.setSoTimeout(0);
		socket.setBroadcast(false);
		while(true){
			DatagramPacket receivePacket = null;
			try{
			byte[] receiveBuffer = new byte[SensorServer.PACKET_SIZE];
			receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			socket.receive(receivePacket);
			} catch (IOException e){
				break;
			}

			String dataMsg = new String(receivePacket.getData()).trim();

			dataMsg = dataMsg.replace(",", ".");

			System.out.println(getClass().getName() + ">> data msg: " + dataMsg);

			if(dataMsg.contains(SensorServer.TEMPERATURE_SENSOR_DATA_VAL)){

				dataMsg = dataMsg.replace(SensorServer.TEMPERATURE_SENSOR_DATA_VAL, "");
				dataMsg = dataMsg.replace("_", "");
				this.sensorServer.writeToProperty(String.valueOf(key), dataMsg);
				key++;

			}else if(dataMsg.equals(SensorServer.TEMPERATURE_SENSOR_READY_MSG)){
				System.out.println(getClass().getName() + " >> Sending subscription to: " + receivePacket.getAddress());
				sensorServer.sendSubscription(receivePacket.getAddress());
			}
		}
		if(socket != null) socket.close();

	}

}
