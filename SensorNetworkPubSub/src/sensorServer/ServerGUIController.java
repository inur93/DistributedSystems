package sensorServer;

public class ServerGUIController {

	private ServerGUI gui;
	private SensorServerController serverController;
	public ServerGUIController(SensorServerController serverController) {
		this.serverController = serverController;
		this.gui = new ServerGUI(this);
		new Thread(this.gui).start();
		
	}

	public void writeToLog(String msg){
		gui.addMsgToLog(msg);
	}
	public void shutdownServer() {
		
	}

	public void restartServer() {
		this.serverController.restartServer();
	}

}
