package ajbc.doodle.calendar.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;

@Service
public class NotificationService {

	@Autowired
	@Qualifier("HTNotificationDao")
	private NotificationDao notificationDao;

	@Autowired
	@Qualifier("HTUserDao")
	private UserDao userDao;

	@Autowired
	@Qualifier("HTEventDao")
	private EventDao eventDao;

	/**
	 * GET operations
	 * 
	 */
	
	public List<Notification> getAllNotifications() throws DaoException {
		return notificationDao.getAllNotifications();
	}

	public Notification getNotificationById(int id) throws DaoException {
		return notificationDao.getNotificationById(id);
	}

	public List<Notification> getNotificationsByIds(List<Integer> ids) throws DaoException {
		List<Notification> notifications = new ArrayList<>();

		for (Integer notificationId : ids) {
			notifications.add(getNotificationById(notificationId));
		}

		return notifications;
	}

	public List<Notification> getNotificationsByEventId(int eventId) throws DaoException {
		// assert that this event exist
		eventDao.getEventById(eventId);
		return notificationDao.getNotificationsByEventId(eventId);
	}

	/**
	 * POST operations
	 * 
	 */

	public void addNotification(Notification notification) throws DaoException {
		notification = checkNotification(notification);
		notificationDao.addNotification(notification);
	}

	public void addNotifications(List<Notification> notifications) throws DaoException {
		List<Notification> notificationsfromDB = new ArrayList<Notification>();

		for (Notification notification : notifications) {
			notificationsfromDB.add(checkNotification(notification));
		}

		notificationDao.addNotifications(notificationsfromDB);
	}

	private Notification checkNotification(Notification notification) throws DaoException {
		int userId = notification.getUserId();
		int eventId = notification.getEventId();
		User user = userDao.getUserById(userId);
		Event event = eventDao.getEventById(eventId);

		// check if -event and user- exist in UserEvent Table
		// if not -> throw exception
		eventDao.getSpecificEventOfUser(userId, eventId);

		notification.setEvent(event);
		notification.setUser(user);

		return notification;
	}

	/**
	 * PUT operations
	 * 
	 */

	public void updateNotification(Notification notification) throws DaoException {
		// assert that notification exists
		getNotificationById(notification.getId());
		notification = checkNotification(notification);
		notificationDao.updateNotification(notification);
	}

	public void updateNotifications(List<Notification> notifications) throws DaoException {
		List<Notification> notificationsfromDB = new ArrayList<Notification>();

		for (Notification notification : notifications) {
			// assert that notification exists
			getNotificationById(notification.getId());
			notification = checkNotification(notification);
			notificationsfromDB.add(notification);
		}

		notificationDao.updateNotifications(notificationsfromDB);
	}

	/**
	 * DELETE operations
	 * 
	 */

}
