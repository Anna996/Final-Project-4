package ajbc.doodle.calendar.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

	/**
	 * GET operations
	 * 
	 */

	public List<Event> getAllEvents() throws DaoException {
		return eventDao.getAllEvents();
	}

	public Event getEventById(int id) throws DaoException {
		return eventDao.getEventById(id);
	}

	public List<Event> getEventsByIds(List<Integer> ids) throws DaoException {
		List<Event> events = new ArrayList<>();

		for (Integer eventId : ids) {
			events.add(eventDao.getEventById(eventId));
		}

		return events;
	}

	public List<Event> getEventsByUserId(int userId) throws DaoException {
		userDao.assertUserExists(userId);
		return eventDao.getEventsByUserId(userId);
	}

	public List<Event> getFutureEventsByUserId(int userId) throws DaoException {
		userDao.assertUserExists(userId);
		return eventDao.getFutureEventsByUserId(userId);
	}

	public List<Event> getEventsInRangeByUserId(int userId, String start, String end) throws DaoException {

		userDao.assertUserExists(userId);

		LocalDateTime startDT, endDT;

		try {
			startDT = Event.parseToLocalDateTime(start);
			endDT = Event.parseToLocalDateTime(end);

		} catch (DateTimeParseException e) {
			throw new DaoException(Event.getFormatExceptionMessage());
		}

		return eventDao.getEventsInRangeByUserId(userId, startDT, endDT);
	}

	public List<Event> getEventsInRange(String start, String end) throws DaoException {
		LocalDateTime startDT, endDT;

		try {
			startDT = Event.parseToLocalDateTime(start);
			endDT = Event.parseToLocalDateTime(end);

		} catch (DateTimeParseException e) {
			throw new DaoException(Event.getFormatExceptionMessage());
		}

		return eventDao.getEventsInRange(startDT, endDT);
	}

	public List<Event> getFutureEventsByUserIdMinutesAndHours(int userId, int minutes, int hours) throws DaoException {
		userDao.assertUserExists(userId);
		return eventDao.getFutureEventsByUserIdMinutesAndHours(userId, minutes, hours);
	}

	/**
	 * POST operations
	 * 
	 */

	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	public void addEvent(Event event, int userId) throws DaoException {

		event = checkEventToAdd(event, userId);
		eventDao.addEvent(event);

		Event fromDB = getEventById(event.getId());
		addDefaultNotification(fromDB, userId);
	}

	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	public void addEvents(List<Event> events, int userId) throws DaoException {
		for (Event event : events) {
			addEvent(event, userId);
		}
	}

	private Event checkEventToAdd(Event event, int userId) throws DaoException {
		User user = userDao.getUserById(userId);

		event.setOwnerId(userId);
		event.addUser(user);
		event.setActive(true);
		return event;
	}

	private void addDefaultNotification(Event event, int userId) throws DaoException {
		User user = userDao.getUserById(userId);
		Notification notification = event.createDefaultNotification(user);
		notificationDao.addNotification(notification);
	}

	/**
	 * PUT operations
	 * 
	 */

	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	public void updateEvent(Event event, int userId) throws DaoException {
		event = checkEventToUpdate(event, userId);
		eventDao.updateEvent(event);
	}

	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	public void updateEvents(List<Event> events, int userId) throws DaoException {
		for (Event event : events) {
			updateEvent(event, userId);
		}

//		eventDao.updateEvents(eventsToUpdate);
	}

	private Event checkEventToUpdate(Event event, int userId) throws DaoException {
		Event fromDB = getEventById(event.getId());

		if (fromDB.getOwnerId() != userId && fromDB.getOwnerId() != event.getOwnerId()) {
			throw new DaoException("only the owner can update this event");
		}

		if (!fromDB.getStart().isEqual(event.getStart())) {
			long secondsDiff = fromDB.getStart().until(event.getStart(), ChronoUnit.SECONDS);
			updateNotificationsStartTime(event.getId(), secondsDiff);
		}

		fromDB.copyEvent(event);

		return fromDB;
	}

	private void updateNotificationsStartTime(int eventId, long seconds) throws DaoException {
		List<Notification> notifications = notificationDao.getNotificationsByEventId(eventId);

		for (Notification notification : notifications) {
			LocalDateTime updatedTime = notification.getLocalDateTime().plusSeconds(seconds);
			notification.setLocalDateTime(updatedTime);
			notificationDao.updateNotification(notification);
		}
	}

	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	public void addGuestsToEvent(int eventId, int userId, List<Integer> guestIds) throws DaoException {
		userDao.assertUserExists(userId);
		Event event = getEventById(eventId);

		if (event.getOwnerId() != userId) {
			throw new DaoException("you are not the owner");
		}

		for (Integer guestId : guestIds) {
			try {
				User guest = userDao.getUserById(guestId);
				event.addUser(guest);
				guest.addEvent(event);
			} catch (DaoException e) {
				throw new DaoException("guest with id [" + guestId + "] doesn't exist in DB");
			}
		}

		eventDao.updateEvent(event);

		for (Integer guestId : guestIds) {
			addDefaultNotification(event, guestId);
		}
	}

	/**
	 * DELETE operations
	 * 
	 */

	public void softDeleteEvent(int id) throws DaoException {
		Event event = getEventById(id);
		eventDao.softDeleteEvent(event);
	}

	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	public void softDeleteEvents(List<Integer> ids) throws DaoException {
		for (Integer id : ids) {
			softDeleteEvent(id);
		}
	}

	public void hardDeleteEvent(int id) throws DaoException {
		Event event = getEventById(id);
		event.setUsers(null);
		eventDao.updateEvent(event);
		eventDao.hardDeleteEvent(eventDao.getEventById(id));
	}

	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	public void hardDeleteEvents(List<Integer> ids) throws DaoException {
		for (Integer id : ids) {
			hardDeleteEvent(id);
		}
	}
}
