package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Notification;

@Repository
@Transactional(readOnly = true, rollbackFor = DaoException.class)
public interface NotificationDao {

	/**
	 * GET operations
	 * 
	 */

	List<Notification> getAllNotifications() throws DaoException;

	Notification getNotificationById(int id) throws DaoException;
	
	List<Notification> getNotificationsByEventId(int eventId) throws DaoException;

	/**
	 * POST operations
	 * 
	 */

	@Transactional(readOnly = false)
	void addNotification(Notification notification) throws DaoException;

	/**
	 * PUT operations
	 * 
	 */

	/**
	 * DELETE operations
	 * 
	 */

}
