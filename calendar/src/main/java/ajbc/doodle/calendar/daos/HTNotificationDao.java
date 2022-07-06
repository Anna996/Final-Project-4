package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.Notification;

@SuppressWarnings("unchecked")
@Repository("HTNotificationDao")
public class HTNotificationDao implements NotificationDao {

	@Autowired
	private HibernateTemplate template;

	@Override
	public List<Notification> getAllNotifications() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Notification.class);
		List<Notification> notifications = (List<Notification>) template.findByCriteria(criteria);

		if (notifications.isEmpty()) {
			throw new DaoException("There are no notifications in DB");
		}

		return notifications;
	}

	@Override
	public Notification getNotificationById(int id) throws DaoException {
		Notification notification = template.get(Notification.class, id);

		if (notification == null) {
			throw new DaoException("There is no such notification in DB");
		}

		return notification;
	}

	@Override
	public void addNotification(Notification notification) throws DaoException {
		try {
			template.persist(notification);
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
	}

}
