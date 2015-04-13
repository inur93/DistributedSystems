package sensorServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class DataHandler implements Runnable{
	
	private DatagramSocket socket;
	private SensorServer sensorServer;

	public DataHandler(SensorServer sensorServer) {
//		this.socket = socket;
		this.sensorServer = sensorServer;
	}

	@Override
	public void run() {
			try{
				socket = new DatagramSocket(SensorServer.RECEIVER_PORT, InetAddress.getByName("0.0.0.0"));
				socket.setBroadcast(false);
				receiveData();
			}catch(IOException e) {
				System.err.println("STOPPED!");
			
		}
	}

	private void receiveData() throws IOException{
	
		int key = 0;
		try{
			socket.setSoTimeout(0);
			socket.setBroadcast(false);
			while(true){

				byte[] receiveBuffer = new byte[SensorServer.PACKET_SIZE];
				DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				socket.receive(receivePacket);

				String dataMsg = new String(receivePacket.getData()).trim();

				dataMsg = dataMsg.replace(",", ".");

				System.out.println("data msg: " + dataMsg);

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
		}catch(IOException e) {
			System.err.println("STOPPED! this");
			e.printStackTrace();
		}
	}

}