package sensor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;import java.net.SocketException;

import common.Constants;

public class SubscriptionReceiver implements Runnable{

	private DatagramSocket socket;
	private SensorController controller;

	public SubscriptionReceiver(SensorController sensorController, DatagramSocket socket){
		this.controller = sensorController;
		this.socket = socket;
	}

	@Override
	public void run() {


		try {
			socket.setBroadcast(false);
		} catch (SocketException e1) {

			controller.addMsgToLog(getClass().getSimpleName() + ">> creating socket failed");
		} 

		while(!controller.terminate){
			byte[] receiveBuffer = new byte[Constants.PACKET_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			try {
				controller.addMsgToLog(getClass().getSimpleName() + ">> Ready to receive subscriptions: " + socket.getLocalSocketAddress());
				System.out.println("test");
				if(socket.isClosed()) break;
				socket.receive(receivePacket);

				controller.addMsgToLog(getClass().getSimpleName() + ">> data received: " + new String(receivePacket.getData()));
				controller.addMsgToLog(getClass().getSimpleName() + ">> received from: " + receivePacket.getSocketAddress());
			} catch (IOException e) {
				controller.addMsgToLog(getClass().getSimpleName() + ">> Receive packet failed - IOException: " + e.getLocalizedMessage());

			}


			String event = new String(receivePacket.getData()).trim();
			this.controller.addMsgToLog(getClass().getSimpleName() + ">> event received: " + event);
			if(event.equals(controller.getTopic() + Constants.SUBSCRIBE_EVENT)){

				this.controller.addSubscriber(receivePacket.getAddress());
				this.controller.addMsgToLog(getClass().getSimpleName() + ">> added subscriber: " + receivePacket.getAddress());
				break;
			}
		}


	}
}



