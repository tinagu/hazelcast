package tina.hazelcast.sample;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4ClassRunner.class)
public class MapSchedulerTest {
	private static Logger log = LoggerFactory.getLogger(MapSchedulerTest.class);
	
	@Test
	public void testSendNotification() throws InterruptedException {
		int count = 10;
		final String userId = "1";
		final String notification = "notification";
		CountDownLatch countLatch = new CountDownLatch(count);
		
		SchedulerTask testGateway = new TestNotificationGateway(countLatch);		
		final MapScheduler scheduler = new MapScheduler(1, TimeUnit.SECONDS, testGateway);
		
		for(int i = 0; i < 10; i++) {
			new Thread(new Runnable() {

				public void run() {
					log.info("put a notification to the scheduler");
					scheduler.put(userId, notification);
				}
				
			}).start();
		}
		
		countLatch.await();

		Assert.assertEquals(0, countLatch.getCount());
	}
	
	class TestNotificationGateway implements SchedulerTask {
		private CountDownLatch latch;		
		
		public TestNotificationGateway(CountDownLatch latch) {
			this.latch = latch;
		}
		
		public void sendOut(String userId, String notification) {
			log.info("TestNotificationGateway: sendout {}", notification);
			latch.countDown();
		}
	}
}
