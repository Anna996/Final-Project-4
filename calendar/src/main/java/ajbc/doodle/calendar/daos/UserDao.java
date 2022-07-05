package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.User;

@Repository
@Transactional(readOnly = true, rollbackFor = DaoException.class)
public interface UserDao {

	List<User> getAllUsers() throws DaoException;
	
	User getUserById(int id) throws DaoException;
	
	@Transactional(readOnly = false)
	void addUser(User user) throws DaoException;
	
	@Transactional(readOnly = false)
	void updateUser(User user) throws DaoException;
}
