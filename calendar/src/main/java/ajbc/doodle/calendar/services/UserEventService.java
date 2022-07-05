package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.daos.UserEventDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.UserEvent;

@Service
public class UserEventService {

	@Autowired
	@Qualifier("HTUserEventDao")
	UserEventDao userEventDao;
	
	
	public List<UserEvent> getAllUserEvents() throws DaoException {
		return userEventDao.getAllUserEvents();
	}
	
	public void addUserEvent(UserEvent userEvent) throws DaoException {
		userEventDao.addUserEvent(userEvent);
	}
}
