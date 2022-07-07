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
public class EventService {

	@Autowired
	@Qualifier("HTEventDao")
	private EventDao eventDao;

	@Autowired
	@Qualifier("HTUserDao")
	private UserDao userDao;

	@Autowired
	@Qualifier("HTNotificationDao")
	private NotificationDao notificationDao;

	public List<Event> getAllEvents() throws DaoException {
		return eventDao.getAllEvents();
	}

	public Event getEventById(int id) throws DaoException {
		return eventDao.getEventById(id);
	}

	public void addEvent(Event event, int userId) throws DaoException {

		User user = userDao.getUserById(userId);
		userDao.assertUserIsLoggedIn(user);

		event.setOwnerId(userId);
		event.addUser(user);
		eventDao.addEvent(event);

		Event fromDB = getEventById(event.getId());

		Notification notification = fromDB.createDefaultNotification(user);
		notificationDao.addNotification(notification);
	}

	public void updateEvent(Event event) throws DaoException {
		eventDao.updateEvent(event);
	}
}
