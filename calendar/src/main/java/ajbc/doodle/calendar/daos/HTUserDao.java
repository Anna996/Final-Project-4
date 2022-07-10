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
import org.springframework.http.ResponseEntity;
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

		assertNotNullable(users);
		return users;
	}

	@Override
	public User getUserById(int id) throws DaoException {
		User user = template.get(User.class, id);
		assertNotNullable(user);

		return user;
	}

	@Override
	public User getUserByEmail(String email) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		criteria.add(Restrictions.eq("email", email));

		List<User> users = (List<User>) template.findByCriteria(criteria);

		if (users.isEmpty()) {
			throw new DaoException("This email doesn't exist in DB");
		}

		return users.get(0);
	}

	@Override
	public boolean emailExists(String email) {
		try {
			// if email doesn't exist - throw DaoException
			getUserByEmail(email);
			return true;
		} catch (DaoException e) {
			return false;
		}
	}

	@Override
	public List<User> getUsersByEventId(int eventId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class, "user");
		criteria.createAlias("user.events", "event", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("event.id", eventId));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<User> users = (List<User>) template.findByCriteria(criteria);

		assertNotNullable(users);
		return users;
	}

	@Override
	public List<User> getUsersWithEventInRange(LocalDateTime start, LocalDateTime end) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class, "user");
		criteria.createAlias("user.events", "event", JoinType.INNER_JOIN);
		criteria.add(Restrictions.ge("event.start", start));
		criteria.add(Restrictions.le("event.end", end));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<User> users = (List<User>) template.findByCriteria(criteria);

		assertNotNullable(users);
		return users;
	}

	/**
	 * POST operations
	 * 
	 */

	@Override
	public void addUser(User user) throws DaoException {
		try {
			template.persist(user);
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
	}

	@Override
	public void addUsers(List<User> users) throws DaoException {
		for (User user : users) {
			template.persist(user);
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

	@Override
	public void updateUsers(List<User> users) throws DaoException {
		try {
			for (User user : users) {
				template.merge(user);
			}
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
	}

	/**
	 * DELETE operations
	 * 
	 */

	/**
	 * Other methods
	 * 
	 */

	// filter: user has his events and also his own notifications for each event
	@Override
	public User filterByUserNotifications(User user) {
		Set<Event> events = user.getEvents().stream().map(event -> event.getCopy(event)).collect(Collectors.toSet());

		events = filterByUserNotifications(events, user.getId());
		user.setEvents(events);

		return user;
	}

	public Set<Event> filterByUserNotifications(Set<Event> events, int userId) {
		events.forEach(event -> {
			Set<Notification> notifications = event.getNotifications().stream()
					.filter(notification -> notification.getUserId() == userId).collect(Collectors.toSet());
			event.setNotifications(notifications);
		});

		return events;
	}

	@Override
	public List<Event> filterByUserNotifications(List<Event> events, int userId) {

		Set<Event> eventSet = events.stream().collect(Collectors.toSet());

		eventSet = filterByUserNotifications(eventSet, userId);

		events = eventSet.stream().collect(Collectors.toList());

		return events;
	}

	@Override
	public List<User> filterByUserNotifications(List<User> users) {
		return users.stream().map(user -> filterByUserNotifications(user)).collect(Collectors.toList());
	}
}
