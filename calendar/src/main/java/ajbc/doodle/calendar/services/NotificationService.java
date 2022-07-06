package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.entities.Notification;

@Service
public class NotificationService {

	@Autowired
	@Qualifier("HTNotificationDao")
	private NotificationDao notificationDao;

	public List<Notification> getAllNotifications() throws DaoException {
		return notificationDao.getAllNotifications();
	}

	public Notification getNotificationById(int id) throws DaoException {
		return notificationDao.getNotificationById(id);
	}

	public void addNotification(Notification notification) throws DaoException {
		notificationDao.addNotification(notification);
	}
}
