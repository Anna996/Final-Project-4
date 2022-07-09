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

	List<User> getUsersByEventId(int eventId) throws DaoException;
	
	List<User> getUsersWithEventInRange(LocalDateTime start, LocalDateTime end) throws DaoException;
	
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
	
	/**
	 * DELETE operations
	 * 
	 */

	
	/**
	 * Default methods
	 * 
	 */
	
	public default void assertNotNullable(User user) throws DaoException {
		if (user == null) {
			throw new DaoException("There is no such user in DB");
		}
	}

	public default void assertUserIsLoggedIn(User user) throws DaoException {
		if (!user.isLoggedIn()) {
			throw new DaoException("you have to log in first");
		}
	}

	public default User approveUserValiditaion(int userId) throws DaoException {
		User user = getUserById(userId);
		assertUserIsLoggedIn(user);
		return user;
	}
}
