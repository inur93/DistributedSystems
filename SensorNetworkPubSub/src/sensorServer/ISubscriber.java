package sensorServer;

import common.Event;

public interface ISubscriber extends Runnable, IController {

	void subscribe(Event event);
	void unsubscribe(Event event);
}
