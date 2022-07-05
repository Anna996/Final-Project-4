package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.Event;

@Repository
public interface EventDao {

	List<Event> getAllEvents() throws DaoException;
}
