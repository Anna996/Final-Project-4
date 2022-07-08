package ajbc.doodle.calendar.services;

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
	private UserDao userDao ;
	
	@Autowired
	@Qualifier("HTEventDao")
	private EventDao eventDao ;
	
	public List<Notification> getAllNotifications() throws DaoException {
		return notificationDao.getAllNotifications();
	}

	public Notification getNotificationById(int id) throws DaoException {
		return notificationDao.getNotificationById(id);
	}

	public void addNotification(Notification notification) throws DaoException {
		int userId = notification.getUserId();
		int eventId = notification.getEventId();
		User user = userDao.approveUserValiditaion(userId);
		Event event = eventDao.getEventById(eventId);
		
		// check if -event and user- exist in UserEvent Table
		// if not -> throw exception
		eventDao.getSpecificEventOfUser(userId, eventId);
		
		notification.setEvent(event);
		notification.setUser(user);
		notificationDao.addNotification(notification);
	}
}
