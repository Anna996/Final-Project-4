package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.UserEvent;

@Repository
public interface UserEventDao {
	
	List<UserEvent> getAllUserEvents() throws DaoException;
}
