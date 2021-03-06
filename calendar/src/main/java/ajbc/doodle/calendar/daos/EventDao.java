package ajbc.doodle.calendar.daos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;

@Repository
@Transactional(readOnly = true, rollbackFor = DaoException.class)
public interface EventDao {

	/**
	 * GET operations
	 * 
	 */

	List<Event> getAllEvents() throws DaoException;

	Event getEventById(int id) throws DaoException;

	List<Event> getEventsByUserId(int userId) throws DaoException;

	Event getSpecificEventOfUser(int userId, int eventId) throws DaoException;

	List<Event> getFutureEventsByUserId(int userId) throws DaoException;

	List<Event> getEventsInRangeByUserId(int userId, LocalDateTime start, LocalDateTime end) throws DaoException;

	List<Event> getEventsInRange(LocalDateTime start, LocalDateTime end) throws DaoException;

	List<Event> getFutureEventsByUserIdMinutesAndHours(int userId, int minutes, int hours) throws DaoException;

	/**
	 * POST operations
	 * 
	 */

	@Transactional(readOnly = false)
	void addEvent(Event event) throws DaoException;

	/**
	 * PUT operations
	 * 
	 */

	@Transactional(readOnly = false)
	void updateEvent(Event event) throws DaoException;

	/**
	 * DELETE operations
	 * 
	 */

	@Transactional(readOnly = false)
	void softDeleteEvent(Event event) throws DaoException;

	@Transactional(readOnly = false)
	void hardDeleteEvent(Event event) throws DaoException;

	/**
	 * Default methods
	 * 
	 */

	default void assertNotNullable(Event event) throws DaoException {
		if (event == null) {
			throw new DaoException("There is no such event in DB");
		}
	}

	default void assertNotNullable(List<Event> events) throws DaoException {
		if (events == null) {
			throw new DaoException("There are no events in DB");
		}
	}
}
