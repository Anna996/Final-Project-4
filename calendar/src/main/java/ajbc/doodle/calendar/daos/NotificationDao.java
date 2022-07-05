package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.Notification;

@Repository
public interface NotificationDao {

	List<Notification> getAllNotifications() throws DaoException;
}
