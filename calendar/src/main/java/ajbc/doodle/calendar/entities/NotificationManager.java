package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.controllers.PushController;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.services.NotificationService;
import ajbc.doodle.calendar.services.UserService;

@Component
public class NotificationManager {
	
	private LocalDateTime nextDateTime;
	private PriorityQueue<Notification> priorityQueue;
	private ScheduledExecutorService scheduledExecutorService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserService userService;

	@Autowired
	private PushController pushController;

	public NotificationManager() {
		priorityQueue = new PriorityQueue<Notification>();
	}

	@EventListener
	public void start(ContextRefreshedEvent event) {
		fetchNotificationFromDB();

		if (!priorityQueue.isEmpty()) {
			updateWakingTime(priorityQueue.peek().getLocalDateTime());
		}
	}

	private void fetchNotificationFromDB() {

		try {
			List<Notification> notifications = notificationService.getAllActivNotifications();
			priorityQueue.addAll(notifications);
		} catch (DaoException e) {
			System.err.println(e.getMessage());
		}
	}

	private void updateWakingTime(LocalDateTime nextDateTime) {

		this.nextDateTime = nextDateTime;

		long secondsToSleep = getSecondsToSleepUntil(nextDateTime);

		if (scheduledExecutorService != null) {
			scheduledExecutorService.shutdownNow();
		}

		scheduledExecutorService = Executors.newScheduledThreadPool(1);
		scheduledExecutorService.schedule(() -> this.run(), secondsToSleep, TimeUnit.SECONDS);
	}

	public void run() {

		List<Notification> readyToRun = new ArrayList<Notification>();
		LocalDateTime timeInAMinute = LocalDateTime.now().plusMinutes(1);

		while (!priorityQueue.isEmpty() && priorityQueue.peek().getLocalDateTime().isBefore(timeInAMinute)) {
			readyToRun.add(priorityQueue.remove());
		}

		ExecutorService executorService = Executors.newFixedThreadPool(readyToRun.size());

		readyToRun.forEach(notification -> executorService
				.execute(new NotificationSender(notification, userService, notificationService, pushController)));

		if (priorityQueue.isEmpty()) {
			this.nextDateTime = null;
		} else {
			updateWakingTime(priorityQueue.peek().getLocalDateTime());
		}
	}

	private long getSecondsToSleepUntil(LocalDateTime dateTime) {
		long secondsToSleep = LocalDateTime.now().until(dateTime, ChronoUnit.SECONDS);
		return secondsToSleep < 1 ? 0 : secondsToSleep;
	}

	public void addNotification(Notification notification) {
		priorityQueue.add(notification);

		if (nextDateTime == null || notification.getLocalDateTime().isBefore(nextDateTime)) {
			updateWakingTime(notification.getLocalDateTime());
		}
	}

	public void addNotifications(List<Notification> notifications) {
		notifications.forEach(notification -> addNotification(notification));
	}

	public void updateNotification(Notification notification) {
		if(priorityQueue.remove(notification) || notification.getLocalDateTime().isAfter(LocalDateTime.now())) {
			addNotification(notification);
		}
	}

	public void updateNotifications(List<Notification> notifications) {
		notifications.forEach(notification -> updateNotification(notification));
	}
}
