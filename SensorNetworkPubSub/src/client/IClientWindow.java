package client;

import java.sql.Timestamp;

import client.ClientWindow.ConnectionStatus;

public interface IClientWindow extends Runnable {

	void setConnectionStatus(ConnectionStatus status);

	void setVisible(boolean visible);
	
	void setMeanVal(double value);
	
	void setMeanTimestamp(Timestamp time);
	
}
