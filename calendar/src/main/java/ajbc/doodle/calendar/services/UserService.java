package ajbc.doodle.calendar.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;

@Service
public class UserService {

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

	public List<User> getAllUsers() throws DaoException {
		return userDao.getAllUsers();
	}

	public User getUserById(int id) throws DaoException {
		return userDao.getUserById(id);
	}

	public List<User> getUsersByIds(List<Integer> ids) throws DaoException {
		List<User> users = new ArrayList<>();

		for (Integer userId : ids) {
			users.add(getUserById(userId));
		}

		return users;
	}

	public User getUserByEmail(String email) throws DaoException {
		return userDao.getUserByEmail(email);
	}

	public List<User> getUsersByEventId(int eventId) throws DaoException {
		// assert that event exists
		eventDao.getEventById(eventId);
		return userDao.getUsersByEventId(eventId);
	}

	public List<User> getUsersWithEventInRange(String start, String end) throws DaoException {

		LocalDateTime startDT, endDT;

		try {
			startDT = Event.parseToLocalDateTime(start);
			endDT = Event.parseToLocalDateTime(end);

		} catch (DateTimeParseException e) {
			throw new DaoException(Event.getFormatExceptionMessage());
		}

		return userDao.getUsersWithEventInRange(startDT, endDT);
	}

	/**
	 * POST operations
	 * 
	 */

	public void addUser(User user) throws DaoException {
		userDao.addUser(user);
		;
	}

	public void addUsers(List<User> users) throws DaoException {
		userDao.addUsers(users);
	}

	/**
	 * PUT operations
	 * 
	 */

	public void updateUser(User user) throws DaoException {
		userDao.updateUser(user);
	}

	/**
	 * DELETE operations
	 * 
	 */
}
