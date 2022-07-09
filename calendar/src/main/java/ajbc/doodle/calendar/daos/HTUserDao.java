package ajbc.doodle.calendar.daos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
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

	/**
	 * GET operations
	 * 
	 */

	@Override
	public List<User> getAllUsers() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<User> users = (List<User>) template.findByCriteria(criteria);

		return filterUserList(users);
	}

	@Override
	public User getUserById(int id) throws DaoException {
		User user = template.get(User.class, id);
		assertNotNullable(user);

		return filterNotificationsOfUser(user);
	}

	@Override
	public User getUserByEmail(String email) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		criteria.add(Restrictions.eq("email", email));

		User user = ((List<User>) template.findByCriteria(criteria)).get(0);
		assertNotNullable(user);

		return filterNotificationsOfUser(user);
	}

	@Override
	public List<User> getUsersByEventId(int eventId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class, "user");
		criteria.createAlias("user.events", "event", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("event.id", eventId));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<User> users = (List<User>) template.findByCriteria(criteria);

		return filterUserList(users);
	}

	@Override
	public List<User> getUsersWithEventInRange(LocalDateTime start, LocalDateTime end) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class, "user");
		criteria.createAlias("user.events", "event", JoinType.INNER_JOIN);
		criteria.add(Restrictions.ge("event.start", start));
		criteria.add(Restrictions.le("event.end", end));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<User> users = (List<User>) template.findByCriteria(criteria);

		return filterUserList(users);
	}
	

	/**
	 * POST operations
	 * 
	 */

	@Override
	public void addUser(User user) throws DaoException {
		User fromDB = getUserByEmail(user.getEmail());
		if (fromDB != null) {
			throw new DaoException("This email already exist in DB: " + user.getEmail());
		}

		try {
			template.persist(user);
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
	}

	@Override
	public void addUsers(List<User> users) throws DaoException {
		for (User user : users) {
			addUser(user);
		}
	}

	/**
	 * PUT operations
	 * 
	 */

	@Override
	public void updateUser(User user) throws DaoException {
		try {
			template.merge(user);
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
	}

	/**
	 * DELETE operations
	 * 
	 */

	/**
	 * private methods
	 * 
	 */

	// filter: user has his events and also his own notifications for each event
	private User filterNotificationsOfUser(User user) {
		Set<Event> events = user.getEvents().stream().map(event -> event.getCopy(event)).collect(Collectors.toSet());

		events.forEach(event -> {
			Set<Notification> notifications = event.getNotifications().stream()
					.filter(notification -> notification.getUserId() == user.getId()).collect(Collectors.toSet());
			event.setNotifications(notifications);
		});

		user.setEvents(events);

		return user;
	}

	private List<User> filterUserList(List<User> users) throws DaoException {
		if (users == null) {
			throw new DaoException("There are no users in DB");
		}

		return users.stream().map(user -> filterNotificationsOfUser(user)).collect(Collectors.toList());
	}
}
