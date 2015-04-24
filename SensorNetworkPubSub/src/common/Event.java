package common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 
 * @author Runi
 *	event class for publish(Event) and Subscribe(Event)
 *	using protocol [TOPIC];[VALUE];[DESTINATION_PORT]; 	get this string by using getEventStr()
 */
public class Event {

	public Topic topic;
	public String value;
	public InetAddress address;
	public int destPort;
	public boolean broadcast;
	
	/**
	 * private convenience method
	 * @param topic has topic as string but also port telling which port to send subscriptions to
	 * @param value can be normal data or the "special" values: READY_VALUE, SUBSCRIBE_VALUE, UNSUBSCRIBE_VALUE
	 * @param address where to send event
	 * @param destPort which port to send event
	 * @param broadcast if broadcast address is not necessary. will broadcast event to destPort
	 */
	private Event(Topic topic, String value, InetAddress address, int destPort, boolean broadcast){
		this.topic = topic;
		this.value = value;
		try {
			// should never throw exception
			this.address = (address == null ? InetAddress.getLocalHost() : address);
		} catch (UnknownHostException e) {
			System.err.println(getClass().getSimpleName() + ">> InetAddress.getLocalHost() failed");
		}
		this.destPort = destPort;
		this.broadcast = broadcast;
	}
	public Event(Topic topic, String value, int destPort, boolean broadcast){
		
		this(topic, value, null, destPort, broadcast);
	}
	public Event(Topic topic, String value, InetAddress address, int destPort){
		this(topic, value, address, destPort, false);
	}
	public String getEventStr() {
		return topic + ";" + value + ";" + topic.port + ";";
	}
	
	public String toString(){
		return "TOPIC[" + topic + "];VALUE["+value+"];BROADCAST["+broadcast+"];ADDRESS["+address.toString()+"]";
	}
}
