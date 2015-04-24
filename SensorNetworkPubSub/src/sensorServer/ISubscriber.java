package sensorServer;

import common.Event;

public interface ISubscriber {

	void subscribe(Event event);
	void unsubscribe(Event event);
}
