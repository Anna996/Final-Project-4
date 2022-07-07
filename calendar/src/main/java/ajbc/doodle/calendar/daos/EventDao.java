package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;

@Repository
@Transactional(readOnly = true, rollbackFor = DaoException.class)
public interface EventDao {

	List<Event> getAllEvents() throws DaoException;
	
	Event getEventById(int id) throws DaoException;
	
	List<Event> getEventsByUserId(int id) throws DaoException;
	
	Event getSpecificEventOfUser(int userId, int eventId) throws DaoException;
	
	@Transactional(readOnly = false)
	void addEvent(Event event) throws DaoException;
	
	@Transactional(readOnly = false)
	void updateEvent(Event event) throws DaoException;
}
