package ajbc.doodle.calendar.entities;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.AppConfig;
import ajbc.doodle.calendar.controllers.PushController;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionKeys;
import ajbc.doodle.calendar.services.NotificationSenderService;
import ajbc.doodle.calendar.services.NotificationService;
import ajbc.doodle.calendar.services.UserService;

public class NotificationSender implements Runnable {

	private Notification notification;
	private UserService userService;
	private NotificationService notificationService;
	private NotificationSenderService notificationSenderService;

	public NotificationSender(Notification notification, UserService userService,
			NotificationService notificationService,  NotificationSenderService notificationSenderService) {
		this.notification = notification;
		this.userService = userService;
		this.notificationSenderService = notificationSenderService;
		this.notificationService = notificationService;
	}

	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	@Override
	public void run() {
		try {
			User user = userService.getUserById(notification.getUserId());

			if (user.isLoggedIn()) {
				Map<String, Subscription> subscriptions = user.getSubscriptions().stream()
						.map(userData -> new Subscription(userData.getEndPoint(), null,
								new SubscriptionKeys(userData.getPublicKey(), userData.getAuth())))
						.collect(Collectors.toMap(sub -> sub.getEndpoint(), sub -> sub));

				notificationService.setNotActive(notification);
				notificationSenderService.sendNotificationToUser(subscriptions, notification.getNotificationForClient());
			}

		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
}
