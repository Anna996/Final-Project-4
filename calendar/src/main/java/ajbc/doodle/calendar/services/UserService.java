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

	public List<User> getAllUsers() throws DaoException {
		return userDao.getAllUsers();
	}

	public User getUserById(int id) throws DaoException {
		return userDao.getUserById(id);
	}
	
	public void addUser(User user) throws DaoException {
		userDao.addUser(user);;
	}

	public void updateUser(User user) throws DaoException {
		userDao.updateUser(user);
	}
}
