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

	public User filterByUserNotifications(User user) {
		return userDao.filterByUserNotifications(user);
	}

	public List<User> filterByUserNotifications(List<User> users) {
		return userDao.filterByUserNotifications(users);
	}

	public List<Event> filterByUserNotifications(List<Event> events, int userId) {
		return userDao.filterByUserNotifications(events, userId);
	}

	/**
	 * POST operations
	 * 
	 */

	public void addUser(User user) throws DaoException {
		checkUserToAdd(user);
		userDao.addUser(user);
	}

	public void addUsers(List<User> users) throws DaoException {
		for (User user : users) {
			checkUserToAdd(user);
		}
		
		userDao.addUsers(users);
	}
	
	private void checkUserToAdd(User user) throws DaoException {
		if (userDao.emailExists(user.getEmail())) {
			throw new DaoException("This email already exist in DB: " + user.getEmail());
		}
	}

	/**
	 * PUT operations
	 * 
	 */

	public void updateUser(User user) throws DaoException {
		user = checkUserToUpdate(user);
		userDao.updateUser(user);
	}

	public void updateUsers(List<User> users) throws DaoException {
		List<User> usersFromDB = new ArrayList<User>();
		
		for (User user : users) {
			usersFromDB.add(checkUserToUpdate(user));
		}
		
		userDao.updateUsers(users);
	}
	
	private User checkUserToUpdate(User user) throws DaoException {
		User fromDB = userDao.getUserById(user.getId());

		if (!fromDB.getEmail().equals(user.getEmail())) {
			if (userDao.emailExists(user.getEmail())) {
				throw new DaoException("The email already exists, try other email");
			}
		}

		fromDB.copyUser(user);
		return fromDB;
	}

	public void updateUserLoggin(String email, boolean isLoggedIn) throws DaoException {
		User user;

		try {
			user = getUserByEmail(email);
		} catch (DaoException e) {
			throw new DaoException("wrong email");
		}

		if (user.isLoggedIn() == isLoggedIn) {
			throw new DaoException("you already logged " + (isLoggedIn ? "in :)" : "out"));
		}

		user.setLoggedIn(isLoggedIn);
		updateUser(user);
	}

	public void loginUser(String email) throws DaoException {
		updateUserLoggin(email, true);
	}

	public void logoutUser(String email) throws DaoException {
		updateUserLoggin(email, false);
	}

	/**
	 * DELETE operations
	 * 
	 */
}
