package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.UserEvent;

@Repository
@Transactional(readOnly = true, rollbackFor = DaoException.class)
public interface UserEventDao {

	List<UserEvent> getAllUserEvents() throws DaoException;
	
	UserEvent getUserEventByIDs(int userId, int eventId) throws DaoException;

	@Transactional(readOnly = false)
	void addUserEvent(UserEvent userEvent) throws DaoException;
}
