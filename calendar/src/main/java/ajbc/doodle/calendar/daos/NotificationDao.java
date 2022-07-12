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
	
	List<Notification> getAllActivNotifications() throws DaoException;

	Notification getNotificationById(int id) throws DaoException;

	List<Notification> getNotificationsByEventId(int eventId) throws DaoException;

	/**
	 * POST operations
	 * 
	 */

	@Transactional(readOnly = false)
	void addNotification(Notification notification) throws DaoException;

	@Transactional(readOnly = false)
	void addNotifications(List<Notification> notifications) throws DaoException;

	/**
	 * PUT operations
	 * 
	 */

	@Transactional(readOnly = false)
	void updateNotification(Notification notification) throws DaoException;

	@Transactional(readOnly = false)
	void updateNotifications(List<Notification> notifications) throws DaoException;

	/**
	 * DELETE operations
	 * 
	 */

	@Transactional(readOnly = false)
	void deleteNotification(Notification notification) throws DaoException;

	@Transactional(readOnly = false)
	void deleteNotifications(List<Notification> notifications) throws DaoException;

	@Transactional(readOnly = false)
	void hardDeleteNotification(Notification notification) throws DaoException;

	@Transactional(readOnly = false)
	void hardDeleteNotifications(List<Notification> notifications) throws DaoException;
}
