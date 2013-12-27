package tina.hazelcast.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSchedulerTask implements SchedulerTask {
	private final static Logger log = LoggerFactory.getLogger(DefaultSchedulerTask.class);

	public void sendOut(String userId, String notification) {
		log.info("DefaultGateway.sendout notification: {} {}", userId, notification);
	}
}
