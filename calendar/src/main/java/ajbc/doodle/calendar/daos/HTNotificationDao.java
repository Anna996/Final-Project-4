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
		return (List<Notification>) template.findByCriteria(criteria);
	}
}
