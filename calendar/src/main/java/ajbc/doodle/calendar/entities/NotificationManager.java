package ajbc.doodle.calendar.entities;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class NotificationManager {

	public NotificationManager() {
		System.out.println("NotificationManager was created");
		this.run();
	}

	public void run() {
		System.out.println("NotificationManager is running...");
	}

	public void stop() {

	}

	public void addNotification(Notification notification) {

	}

	public void addNotifications(List<Notification> notifications) {

	}

	public void updateNotification(Notification notification) {

	}

	public void updateNotifications(List<Notification> notifications) {

	}
}
