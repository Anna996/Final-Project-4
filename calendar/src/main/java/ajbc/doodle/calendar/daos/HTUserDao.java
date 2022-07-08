package ajbc.doodle.calendar.daos;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;

@SuppressWarnings("unchecked")
@Repository("HTUserDao")
public class HTUserDao implements UserDao {

	@Autowired
	private HibernateTemplate template;

	@Override
	public List<User> getAllUsers() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<User> users = (List<User>) template.findByCriteria(criteria);

		if (users.isEmpty()) {
			throw new DaoException("There are no users in DB");
		}
		
		// filter: user has his events and also his own notifications for each event
		users.forEach(user -> {
			
			Set<Event> events = user.getEvents().stream().map(event -> event.getCopy(event)).collect(Collectors.toSet());
			
			events.forEach(event -> {
				Set<Notification> notifications = event.getNotifications().stream().filter(notification -> notification.getUserId() == user.getId()).collect(Collectors.toSet());
				event.setNotifications(notifications);
			});
			
			user.setEvents(events);
		});

		return users;
	}

	@Override
	public User getUserById(int id) throws DaoException {
		User user = template.get(User.class, id);

		if (user == null) {
			throw new DaoException("There is no such user in DB");
		}

		return user;
	}

	@Override
	public void addUser(User user) throws DaoException {
		try {
			template.persist(user);
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
	}

	@Override
	public void updateUser(User user) throws DaoException {
		try {
			template.merge(user);
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
	}

}
