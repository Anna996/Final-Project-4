package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

@Component
public class NotificationManager {

	private PriorityQueue<Notification> priorityQueue;

	public NotificationManager() {
		System.out.println("NotificationManager was created");

		priorityQueue = new PriorityQueue<Notification>();

//		this.run();
	}

	public void run() {
		System.out.println("NotificationManager is running...");
		
		List<Notification> readyToRun = new ArrayList<Notification>();
		LocalDateTime timeInAMinute = LocalDateTime.now().plusMinutes(1);
		
		while(priorityQueue.peek().getLocalDateTime().isBefore(timeInAMinute)) {
			readyToRun.add(priorityQueue.remove());
		}
		
		ExecutorService executorService = Executors.newFixedThreadPool(readyToRun.size());
		
		readyToRun.forEach(notification -> executorService.execute(new NotificationSender(notification)));
		
		// the next time to wake up
		long secondsToSleep = LocalDateTime.now().until(priorityQueue.peek().getLocalDateTime(),ChronoUnit.SECONDS);
	}

	public void stop() {

	}

	public void addNotification(Notification notification) {
		priorityQueue.add(notification);
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
