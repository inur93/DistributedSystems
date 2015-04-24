package common;

public class Topic {
	public String topic;
	public int port;
	public Topic(String topic, int port) {
		this.topic = topic;
		this.port = port;
	}

	public String toString(){
		return topic;
	}
}
