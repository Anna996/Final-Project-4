package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.daos.UserEventDao;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.UserEvent;

@Service
public class UserService {

	@Autowired
	@Qualifier("HTUserDao")
	UserDao userDao;
	
	// TODO: remove. 
	@Autowired
	@Qualifier("HTUserEventDao")
	UserEventDao userEventDao;
	
	public List<User> getAllUsers() throws DaoException {
		List<User> users = userDao.getAllUsers();
	
		if (users.isEmpty()) {
			throw new DaoException("There are no users in DB");
		}

		return users;
	}
	
	
	// TODO: remove. 
	public List<UserEvent> getAllUserEvents() throws DaoException {
		List<UserEvent> usersEvents = userEventDao.getAllUserEvents();
	
		if (usersEvents.isEmpty()) {
			throw new DaoException("There are no user-events in DB");
		}

		return usersEvents;
	}
}
