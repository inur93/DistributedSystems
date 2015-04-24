package common;

public class Constants {

	public static final String DEFAULT_NAME =  "255.255.255.255"; //"192.168.10.255";//
	
	public static final int PUBLISHER_PORT = 8889;
	public static final int SUBSCRBER_PORT = 8888;
	
	public static final String FILE_NAME = "temperature";
	
	public static enum Topics{TEMP, LIGHT};
	public static final String READY_EVENT = "READY";
	public static final String SUBSCRIBE_VALUE = "SUBSCRIBE";
	public static final String UNSUBSCRIBE_VALUE = "UNSUBSCRIBE";

	public static final int PACKET_SIZE = 512;
	public static final int DATA_SIZE = 5;
	
	
	
	
}
