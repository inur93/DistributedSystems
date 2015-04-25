package common;

public class Constants {

	public static final String DEFAULT_NAME =  "255.255.255.255"; //"192.168.10.255";//
	
	/**
	 * is no longer used since publisher listener port is defined by topic
	 */
	@Deprecated
	public static final int PUBLISHER_PORT = 8889; 
	
	public static final int SUBSCRIBER_PORT = 8888;
	
	// values for different events
	public static final String READY_VALUE = "READY";
	public static final String SUBSCRIBE_VALUE = "SUBSCRIBE";
	public static final String UNSUBSCRIBE_VALUE = "UNSUBSCRIBE";
	public static final String TEMP_DATA_VALUE = "\\d{2}.\\d{2}";
		
	public static final Topic TEST_TEMP_TOPIC = new Topic("TEMP", 8900);
	public static final Topic TEST_LIGHT_TOPIC = new Topic("LIGHT", 8901);
	
	public static final int PACKET_SIZE = 512;
	public static final int DATA_SIZE = 5;

	public static final long SUBSCRIPTION_TIMEOUT = 30000; // milliseconds
	public static final long SUBSCRIPTION_BROADCAST_INTERVAL = 10000; // milliseconds 


	
	
	
	
}
