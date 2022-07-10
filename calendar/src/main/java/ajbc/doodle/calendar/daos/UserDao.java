package ajbc.doodle.calendar.daos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;

@Repository
@Transactional(readOnly = true, rollbackFor = DaoException.class)
public interface UserDao {

	/**
	 * GET operations
	 * 
	 */

	List<User> getAllUsers() throws DaoException;

	User getUserById(int id) throws DaoException;

	User getUserByEmail(String email) throws DaoException;

	boolean emailExists(String email);
	
	List<User> getUsersByEventId(int eventId) throws DaoException;

	List<User> getUsersWithEventInRange(LocalDateTime start, LocalDateTime end) throws DaoException;

	User filterByUserNotifications(User user);

	List<User> filterByUserNotifications(List<User> users);
	
	List<Event> filterByUserNotifications(List<Event> events, int userId);

	
	/**
	 * POST operations
	 * 
	 */

	@Transactional(readOnly = false)
	void addUser(User user) throws DaoException;

	@Transactional(readOnly = false)
	void addUsers(List<User> users) throws DaoException;

	/**
	 * PUT operations
	 * 
	 */

	@Transactional(readOnly = false)
	void updateUser(User user) throws DaoException;
	
	@Transactional(readOnly = false)
	void updateUsers(List<User> users) throws DaoException;

	/**
	 * DELETE operations
	 * 
	 */
	
	@Transactional(readOnly = false)
	void deleteUser(User user) throws DaoException;
	
	@Transactional(readOnly = false)
	void hardDeleteUser(User user) throws DaoException;

	/**
	 * Default methods
	 * 
	 */

	default void assertNotNullable(User user) throws DaoException {
		if (user == null) {
			throw new DaoException("There is no such user in DB");
		}
	}
	
	default void assertNotNullable(List<User> users) throws DaoException {
		if (users == null) {
			throw new DaoException("There are no users in DB");
		}
	}

	default void assertUserIsLoggedIn(User user) throws DaoException {
		if (!user.isLoggedIn()) {
			throw new DaoException("you have to log in first");
		}
	}

	default void assertUserExists(int userId) throws DaoException {
		// if user doesn't exist, this method will throw DaoException
		getUserById(userId);
	}
}
