package common;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Event {

	public String topic;
	public String value;
	public InetAddress address;
	public int port;
	public boolean broadcast;
	
	private Event(String topic, String value, InetAddress address, int port, boolean broadcast){
		this.topic = topic;
		this.value = value;
		try {
			this.address = (address == null ? InetAddress.getLocalHost() : address);
		} catch (UnknownHostException e) {
			System.err.println("all is destroyed :(");
		}
		this.port = port;
		this.broadcast = broadcast;
	}
	public Event(String topic, String value, int port, boolean broadcast){
		
		this(topic, value, null, port, broadcast);
	}
	public Event(String topic, String value, InetAddress address, int port){
		this(topic, value, address, port, false);
	}
	public String getEventStr() {
		return topic + ";" + value + ";";
	}
	
	public String toString(){
		return "TOPIC[" + topic + "];VALUE["+value+"];BROADCAST["+broadcast+"];ADDRESS["+address.toString()+"]";
	}
}
