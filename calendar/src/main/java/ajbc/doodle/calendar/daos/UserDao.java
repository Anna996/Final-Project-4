package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.User;

@Repository
public interface UserDao {

	List<User> getAllUsers() throws DaoException;
}
