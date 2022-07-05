package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.entities.Event;

@Service
public class EventService {

	@Autowired
	@Qualifier("HTEventDao")
	EventDao EventDao;
	
	public List<Event> getAllEvents() throws DaoException {
		return EventDao.getAllEvents();
	}
}
