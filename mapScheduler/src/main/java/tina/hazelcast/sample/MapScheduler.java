package tina.hazelcast.sample;

import java.util.concurrent.TimeUnit;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapScheduler {
	private static Logger log = LoggerFactory.getLogger(MapScheduler.class);
	private IMap<String, NotificationQueue> schedulerMap;
	private final static String MAP_SCHEDULER_NAME = "tina_mapScheduler";
	private final int interval;
	private final TimeUnit timeUnit;
	private SchedulerTask schedulerTask;
	private Object syncMap = new Object();
	
	public MapScheduler(int interval, TimeUnit timeUnit, SchedulerTask schedulerTask) {
		HazelcastInstance hazelcastInstance = HazelcastLoader.load();
		schedulerMap = hazelcastInstance.getMap(MAP_SCHEDULER_NAME);
		schedulerMap.addEntryListener(new MapEntryListener(), true);
		this.interval = interval;
		this.timeUnit = timeUnit;
		this.schedulerTask = schedulerTask;
	}
	
	public void put(String userId, String notification) {
		NotificationQueue queue = null;
		log.debug("put user: {}, notification: {}", userId, notification);
		synchronized(syncMap) {
			if(schedulerMap.containsKey(userId)) {
				log.debug("put notification into exisiting queue");
				queue = schedulerMap.get(userId);
			}
			else {
				log.debug("put notification into a new queue");
				queue = new NotificationQueue();
			}
			queue.offer(notification);
			schedulerMap.putAsync(userId, queue, interval, timeUnit);			
		}
	}
	
	public void shutdown() {
		Hazelcast.shutdownAll();
	}
	
	class MapEntryListener implements EntryListener<String, NotificationQueue> {

		public void entryAdded(EntryEvent<String, NotificationQueue> event) {
			log.debug("entryAdded: {}", event.getKey());
		}

		public void entryRemoved(EntryEvent<String, NotificationQueue> event) {
		}

		public void entryUpdated(EntryEvent<String, NotificationQueue> event) {			
		}

		public void entryEvicted(EntryEvent<String, NotificationQueue> event) {
			String userId = event.getKey();
			log.info("MapScheduler.MapEntryListener.entryEvicted for {}", userId);
			
			try {
				NotificationQueue queue = event.getValue();
				
				String notification = queue.poll();
				log.info("retrieve notification from the queue: {}", notification);
				if(null == notification) {
					log.info("there is no notification.");
					return;
				}
				schedulerTask.sendOut(userId, notification);
				
				if(null != queue) {
					log.info("there are remaining notifications in the queue, put the queue back.");
					schedulerMap.putAsync(userId, queue, interval, timeUnit);
				}
				
			} catch (Exception e) {
				log.error("failed to handle evicted notification", e);
			}
		}		
	}
}
