package sensorServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import common.PropertyHelper;

public class DataHandler implements Runnable{
	public static final String DATA_RECEIVE_MSG = "DATA";
	private DatagramSocket socket;
	private SensorServer sensorServer;
	private int PORT = 8888;
	public DataHandler(SensorServer sensorServer) {
		this.sensorServer = sensorServer;
	}

	@Override
	public void run() {
		boolean socketFailed = false;
		while(socketFailed){
			try{
				socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
				socket.setBroadcast(true);
				receiveData();
			}catch(IOException e) {
				System.err.println("STOPPED!");
				System.err.println("RECREATING SOCKET on port: " + PORT);
				socketFailed = true;
				PORT++;
			}
		}
	}

	private void receiveData() throws IOException{
		int key = 0;
		while(true){
			System.out.println(getClass().getName() + " >> Ready to receive broadcast packets!");

			byte[] receiveBuffer = new byte[15000];
			DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			socket.receive(receivePacket);

			System.out.println(getClass().getName() + " >>Discovery packet received from: " + receivePacket.getAddress().getHostAddress());
			System.out.println(getClass().getName() + " >>Packet received; data: " + new String(receivePacket.getData()));

			String[] rawData = new String(receivePacket.getData()).trim().split("_");
			String dataMsg = "";
			String dataVal = "";
			if(rawData.length > 1){
				dataMsg = rawData[0];
				dataVal = rawData[1];
			}
			if(dataMsg.equals(DATA_RECEIVE_MSG)){
				System.out.println(getClass().getName()+ " >>measurement received from: " + receivePacket.getAddress().getHostAddress());
				this.sensorServer.writeToProperty(String.valueOf(key), dataVal);
				key++;
			}
		}
	}

}
