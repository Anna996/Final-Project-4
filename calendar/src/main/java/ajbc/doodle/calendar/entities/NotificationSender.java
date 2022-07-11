package ajbc.doodle.calendar.entities;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ajbc.doodle.calendar.AppConfig;
import ajbc.doodle.calendar.controllers.PushController;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionKeys;

public class NotificationSender implements Runnable {

	private Notification notification;

	private static AnnotationConfigApplicationContext ctx;
	private static PushController pushController;

	static {
		ctx = new AnnotationConfigApplicationContext(AppConfig.class);
		pushController = ctx.getBean(PushController.class);
	}

	public NotificationSender(Notification notification) {
		this.notification = notification;
	}

	@Override
	public void run() {
		User user = notification.getUser();

		if (user.isLoggedIn()) {

			Map<String, Subscription> subscriptions = user.getSubscriptions().stream()
					.map(userData -> new Subscription(userData.getEndPoint(), null,
							new SubscriptionKeys(userData.getPublicKey(), userData.getAuth())))
					.collect(Collectors.toMap(sub -> sub.getEndpoint(), sub -> sub));

			pushController.sendNotificationToUser(subscriptions, notification.getNotificationForClient());
		}
	}
}
