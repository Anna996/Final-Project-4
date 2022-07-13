package ajbc.doodle.calendar.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	private final int MAX_NOTIFICATIONS_PER_EVENT = 10;

	/**
	 * GET operations
	 * 
	 */

	public List<Notification> getAllNotifications() throws DaoException {
		return notificationDao.getAllNotifications();
	}

	public List<Notification> getAllActivNotifications() throws DaoException {
		return notificationDao.getAllActivNotifications();
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
		notification.setActive(true);
		notificationDao.addNotification(notification);
	}

	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	public void addNotifications(List<Notification> notifications) throws DaoException {
		for (Notification notification : notifications) {
			addNotification(notification);
		}
	}

	private Notification checkNotification(Notification notification) throws DaoException {
		int userId = notification.getUserId();
		int eventId = notification.getEventId();
		User user = userDao.getUserById(userId);
		Event event = eventDao.getEventById(eventId);

		// check if -event and user- exist in UserEvent Table
		// if not -> throw exception
		eventDao.getSpecificEventOfUser(userId, eventId);

		// check if this user has less then 10 notifications for this event
		Long existNotifications = notificationDao.getNumNotificationsForUserEvent(eventId, userId);
		if (existNotifications == MAX_NOTIFICATIONS_PER_EVENT) {
			throw new DaoException("you already have " + MAX_NOTIFICATIONS_PER_EVENT
					+ " notifications - this is the maximum per event");
		}

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

	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	public void updateNotifications(List<Notification> notifications) throws DaoException {
		for (Notification notification : notifications) {
			updateNotification(notification);
		}
	}

	public void setNotActive(Notification notification) throws DaoException {
		notificationDao.softDeleteNotification(notification);
	}

	/**
	 * DELETE operations
	 * 
	 */

	public void softDeleteNotification(int id) throws DaoException {
		Notification notification = notificationDao.getNotificationById(id);
		notificationDao.softDeleteNotification(notification);
	}

	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	public void softDeleteNotifications(List<Integer> notificationIds) throws DaoException {
		for (Integer id : notificationIds) {
			softDeleteNotification(id);
		}
	}

	public void hardDeleteNotification(int id) throws DaoException {
		Notification notification = notificationDao.getNotificationById(id);
		notificationDao.hardDeleteNotification(notification);
	}

	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	public void hardDeleteNotifications(List<Integer> notificationIds) throws DaoException {
		for (Integer id : notificationIds) {
			hardDeleteNotification(id);
		}
	}
}
