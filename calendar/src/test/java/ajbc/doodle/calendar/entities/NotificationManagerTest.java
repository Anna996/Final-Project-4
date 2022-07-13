package ajbc.doodle.calendar.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class NotificationManagerTest {

	private NotificationManager manager;

	public NotificationManagerTest() {
		manager = new NotificationManager();
	}

	@Test
	void testAddNotification() {
		Notification notification = new Notification();
		LocalDateTime now = LocalDateTime.now();

		notification.setLocalDateTime(now);

		// manager.userService is null because it is @Autowired
		// so, executorService in run method throws NullPointerException.
		manager.addNotification(notification);

		assertEquals(now, manager.getNextDateTime());
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
		assertTrue(manager.getSecondsToSleepUntil(inOneSecond) == 1);
		
		LocalDateTime in60Seconds = LocalDateTime.now().plusSeconds(60);
		assertTrue(manager.getSecondsToSleepUntil(in60Seconds) == 60);
		
		LocalDateTime oneSecondBeforeNow = LocalDateTime.now().minusSeconds(1);
		assertTrue(manager.getSecondsToSleepUntil(oneSecondBeforeNow) == 0);
		
		LocalDateTime oneHourBeforeNow = LocalDateTime.now().minusHours(1);
		assertTrue(manager.getSecondsToSleepUntil(oneHourBeforeNow) == 0);
	}

	@Test
	void testUpdateNotification() {
		// TODO 
	}
}
