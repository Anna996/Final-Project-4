package ajbc.doodle.calendar.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
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

	public void addEvent(Event event, int userId) throws DaoException {

		event = checkEventToAdd(event, userId);
		eventDao.addEvent(event);

		Event fromDB = getEventById(event.getId());
		addDefaultNotification(fromDB);
	}

	public void addEvents(List<Event> events, int userId) throws DaoException {
		List<Event> eventsToAdd = new ArrayList<Event>();

		for (Event event : events) {
			eventsToAdd.add(checkEventToAdd(event, userId));
		}

		eventDao.addEvents(eventsToAdd);

		for (Event event : eventsToAdd) {
			addDefaultNotification(event);
		}
	}

	private Event checkEventToAdd(Event event, int userId) throws DaoException {
		User user = userDao.getUserById(userId);

		event.setOwnerId(userId);
		event.addUser(user);
		return event;
	}

	private void addDefaultNotification(Event event) throws DaoException {
		User user = userDao.getUserById(event.getOwnerId());
		Notification notification = event.createDefaultNotification(user);
		notificationDao.addNotification(notification);
	}

	/**
	 * PUT operations
	 * 
	 */

	public void updateEvent(Event event, int userId) throws DaoException {
		event = checkEventToUpdate(event, userId);
		eventDao.updateEvent(event);
	}

	public void updateEvents(List<Event> events, int userId) throws DaoException {
		List<Event> eventsToUpdate = new ArrayList<Event>();

		for (Event event : events) {
			eventsToUpdate.add(checkEventToUpdate(event, userId));
		}
		
		eventDao.updateEvents(eventsToUpdate);
	}

	private Event checkEventToUpdate(Event event, int userId) throws DaoException {
		Event fromDB = getEventById(event.getId());

		if (fromDB.getOwnerId() != userId && fromDB.getOwnerId() != event.getOwnerId()) {
			throw new DaoException("only the owner can update this event");
		}

		fromDB.copyEvent(event);
		
		return fromDB;
	}

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
	}

	/**
	 * DELETE operations
	 * 
	 */

	/**
	 * Other methods
	 * 
	 */
	

}
