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

	private PriorityQueue<Notification> priorityQueue;

	private long secondsToSleep;
	private ScheduledExecutorService scheduledExecutorService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserService userService;

	@Autowired
	private PushController pushController;

	public NotificationManager() {
		priorityQueue = new PriorityQueue<Notification>();
		scheduledExecutorService = Executors.newScheduledThreadPool(1);
	}

	@EventListener
	public void start(ContextRefreshedEvent event) {
		fetchNotificationFromDB();
		secondsToSleep = getSecondsToSleepUntilNextNotification();
		updateWakingTime(secondsToSleep);
	}

	// TODO change to not deleted notifications
	private void fetchNotificationFromDB() {

		try {
			List<Notification> notifications = notificationService.getAllNotifications();
			priorityQueue.addAll(notifications);
		} catch (DaoException e) {
			System.err.println(e.getMessage());
		}
	}

	public void updateWakingTime(long secondsToSleep) {
		if (secondsToSleep == -1l) {
			return;
		}

		// TODO check if there is a need to update secondsToSleep

//		scheduledExecutorService.shutdownNow();
		scheduledExecutorService.schedule(() -> this.run(), 5, TimeUnit.SECONDS);
	}

	public void run() {

		if (priorityQueue.isEmpty()) {
			return;
		}

		List<Notification> readyToRun = new ArrayList<Notification>();
		LocalDateTime timeInAMinute = LocalDateTime.now().plusMinutes(1);

//		while (!priorityQueue.isEmpty() && priorityQueue.peek().getLocalDateTime().isBefore(timeInAMinute)) {
//			readyToRun.add(priorityQueue.remove());
//		}

		readyToRun.add(priorityQueue.remove());

		ExecutorService executorService = Executors.newFixedThreadPool(readyToRun.size());

		readyToRun.forEach(notification -> executorService
				.execute(new NotificationSender(notification, userService, pushController)));

		// the next time to wake up
		updateWakingTime(getSecondsToSleepUntilNextNotification());
	}

//	@Override
//	public void run() {
//
////		NotificationSender sender = new NotificationSender(priorityQueue.remove(),userService, pushController );
//		ExecutorService executorService = Executors.newFixedThreadPool(1);
//		executorService.execute(new NotificationSender(priorityQueue.remove(),userService, pushController));
//		
////		sender.run();
//	}

	public long getSecondsToSleepUntilNextNotification() {
		if (priorityQueue.isEmpty()) {
			return -1;
		}

		return LocalDateTime.now().until(priorityQueue.peek().getLocalDateTime(), ChronoUnit.SECONDS);
	}

	public void addNotification(Notification notification) {
	}

	public void addNotifications(List<Notification> notifications) {
		notifications.forEach(notification -> addNotification(notification));
	}

	public void updateNotification(Notification notification) {

	}

	public void updateNotifications(List<Notification> notifications) {
		notifications.forEach(notification -> updateNotification(notification));
	}
}
