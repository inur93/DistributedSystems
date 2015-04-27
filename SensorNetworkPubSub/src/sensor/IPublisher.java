package sensor;

import sensorServer.IController;
import common.Event;

public interface IPublisher extends Runnable, IController {

	boolean isTerminated();
	void publish(Event event);
}
