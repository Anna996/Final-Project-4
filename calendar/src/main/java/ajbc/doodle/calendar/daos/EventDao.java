package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Event;

@Repository
@Transactional(readOnly = true, rollbackFor = DaoException.class)
public interface EventDao {

	List<Event> getAllEvents() throws DaoException;
	
	Event getEventById(int id) throws DaoException;
	
	@Transactional(readOnly = false)
	void addEvent(Event event) throws DaoException;
}
