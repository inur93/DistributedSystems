package common;

import java.net.InetAddress;
import java.sql.Timestamp;

public class Subscription {

	public InetAddress address;
	public Timestamp timestamp;
	public Subscription(InetAddress address, Timestamp timestamp) {
		this.address = address;
		this.timestamp = timestamp;
	}
	
	public String toString(){
		return address.toString();
	}

}
