package sensorServer.middleware;

import java.net.InetAddress;

public interface MiddlewareInterface {
	void publish(String data);
	boolean subscribe(InetAddress address, String msg);
}
