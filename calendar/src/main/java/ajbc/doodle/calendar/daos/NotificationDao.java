package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Notification;

@Repository
@Transactional(readOnly = true, rollbackFor = DaoException.class)
public interface NotificationDao {

	List<Notification> getAllNotifications() throws DaoException;

	Notification getNotificationById(int id) throws DaoException;

	@Transactional(readOnly = false)
	void addNotification(Notification notification) throws DaoException;
}
