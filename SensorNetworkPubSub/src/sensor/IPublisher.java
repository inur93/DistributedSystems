package sensor;

import common.Event;

public interface IPublisher extends Runnable {

	boolean isTerminated();
	void publish(Event event);
}
