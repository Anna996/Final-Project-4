//package ajbc.doodle.calendar.entities;
//
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//@Component
//public class WakeupManager {
//
//	@Autowired
//	private NotificationManager notificationManager;
//	
//	private long secondsToSleep;
//	private ScheduledExecutorService scheduledExecutorService;
//	
//	
//	public WakeupManager() {
//		scheduledExecutorService = Executors.newScheduledThreadPool(1);
//	}
//	
//	@EventListener
//	@Order(1)
//	public void updateNotificationManager(ContextRefreshedEvent event) {
//		notificationManager.setWakeupManager(this);
//	}
//
//	public void start() {
//		secondsToSleep = notificationManager.getSecondsToSleepUntilNextNotification();
//		updateWakingTime(secondsToSleep);
//	}
//	
//	public void updateWakingTime(long secondsToSleep) {
//		if (secondsToSleep == -1l) {
//			return;
//		}
//
//		// TODO check if there is a need to update secondsToSleep
//
////		scheduledExecutorService.shutdownNow();
//		scheduledExecutorService.schedule(notificationManager, 5, TimeUnit.SECONDS);
//	}
//	
//	
//	public void stop() {
//
//	}
//	
//	public void addNotification(Notification notification) {
//	}
//
//	public void addNotifications(List<Notification> notifications) {
//		notifications.forEach(notification -> addNotification(notification));
//	}
//
//	public void updateNotification(Notification notification) {
//
//	}
//
//	public void updateNotifications(List<Notification> notifications) {
//		notifications.forEach(notification -> updateNotification(notification));
//	}
//}
