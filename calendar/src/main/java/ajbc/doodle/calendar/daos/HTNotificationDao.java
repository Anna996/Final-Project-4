package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.Notification;

@SuppressWarnings("unchecked")
@Repository("HTNotificationDao")
public class HTNotificationDao implements NotificationDao {

	@Autowired
	private HibernateTemplate template;

	/**
	 * GET operations
	 * 
	 */

	@Override
	public List<Notification> getAllNotifications() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Notification.class);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		List<Notification> notifications = (List<Notification>) template.findByCriteria(criteria);
		assertNotificationListNotNullable(notifications);

		return notifications;
	}

	@Override
	public List<Notification> getAllActivNotifications() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Notification.class);
		criteria.add(Restrictions.eq("isActive", true));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		List<Notification> notifications = (List<Notification>) template.findByCriteria(criteria);
		assertNotificationListNotNullable(notifications);

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
	public List<Notification> getNotificationsByEventId(int eventId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Notification.class);
		criteria.add(Restrictions.eq("eventId", eventId));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		List<Notification> notifications = (List<Notification>) template.findByCriteria(criteria);
		assertNotificationListNotNullable(notifications);

		return notifications;
	}

	/**
	 * POST operations
	 * 
	 */

	@Override
	public void addNotification(Notification notification) throws DaoException {
		template.persist(notification);
	}

	@Override
	public void addNotifications(List<Notification> notifications) throws DaoException {
		for (Notification notification : notifications) {
			template.persist(notification);
		}
	}

	/**
	 * PUT operations
	 * 
	 */

	@Override
	public void updateNotification(Notification notification) throws DaoException {
		template.merge(notification);
	}

	@Override
	public void updateNotifications(List<Notification> notifications) throws DaoException {
		for (Notification notification : notifications) {
			template.merge(notification);
		}
	}

	/**
	 * DELETE operations
	 * 
	 */

	@Override
	public void deleteNotification(Notification notification) throws DaoException {
		notification.setActive(false);
		template.merge(notification);
	}

	@Override
	public void deleteNotifications(List<Notification> notifications) throws DaoException {
		for (Notification notification : notifications) {
			notification.setActive(false);
			template.merge(notification);
		}
	}

	@Override
	public void hardDeleteNotification(Notification notification) throws DaoException {
		template.delete(notification);

	}

	@Override
	public void hardDeleteNotifications(List<Notification> notifications) throws DaoException {
		for (Notification notification : notifications) {
			template.delete(notification);
		}
	}

	/**
	 * Other operations
	 * 
	 */

	public void assertNotificationListNotNullable(List<Notification> notifications) throws DaoException {
		if (notifications == null) {
			throw new DaoException("There are no notifications in DB");
		}
	}
}
