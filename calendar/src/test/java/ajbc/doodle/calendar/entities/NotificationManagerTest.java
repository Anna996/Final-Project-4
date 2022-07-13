package ajbc.doodle.calendar.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_METHOD)
class NotificationManagerTest {

	private NotificationManager manager;


	public NotificationManagerTest() {
		manager = new NotificationManager();
	}

	@Test
	void testAddNotification() throws InterruptedException {
		Notification notification = new Notification();
		LocalDateTime now = LocalDateTime.now();

		notification.setLocalDateTime(now);

		// manager.userService is null because it is @Autowired
		// so, executorService in run method throws NullPointerException.
		manager.addNotification(notification);

		assertEquals(now, manager.getNextDateTime());
		// we go to sleep, because there is another thread who needs to dequeue this notification from the queue, 
		// and sometimes we arrive to this line before the thread ran.
		Thread.sleep(500);
		assertTrue(manager.getPriorityQueue().isEmpty());

		LocalDateTime inOneHour = LocalDateTime.now().plusHours(1);
		notification.setLocalDateTime(inOneHour);
		manager.addNotification(notification);
		assertEquals(inOneHour, manager.getNextDateTime());
		assertTrue(manager.getPriorityQueue().peek() == notification);
	}

	@Test
	void testGetSecondsToSleepUntil() {
		LocalDateTime inOneSecond = LocalDateTime.now().plusSeconds(1);
		assertTrue(manager.getSecondsToSleepUntil(inOneSecond) <= 1);

		LocalDateTime in60Seconds = LocalDateTime.now().plusSeconds(60);
		long seconds = manager.getSecondsToSleepUntil(in60Seconds);
		assertTrue(seconds > 55 && seconds <= 60);

		LocalDateTime oneSecondBeforeNow = LocalDateTime.now().minusSeconds(1);
		assertTrue(manager.getSecondsToSleepUntil(oneSecondBeforeNow) == 0);

		LocalDateTime oneHourBeforeNow = LocalDateTime.now().minusHours(1);
		assertTrue(manager.getSecondsToSleepUntil(oneHourBeforeNow) == 0);
	}

	@Test
	void testUpdateNotification() {
		Notification notification = new Notification();
		LocalDateTime in60Seconds = LocalDateTime.now().plusSeconds(60);

		notification.setLocalDateTime(in60Seconds);

		manager.updateNotification(notification);
		assertEquals(in60Seconds, manager.getNextDateTime());
		assertTrue(manager.getPriorityQueue().peek() == notification);
	}
}
