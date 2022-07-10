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

@SuppressWarnings("unchecked")
@Repository("HTEventDao")
public class HTEventDao implements EventDao {

	@Autowired
	private HibernateTemplate template;

	/**
	 * GET operations
	 * 
	 */

	@Override
	public List<Event> getAllEvents() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		List<Event> events = (List<Event>) template.findByCriteria(criteria);
		assertNotNullable(events);

		return events;
	}

	@Override
	public Event getEventById(int id) throws DaoException {
		Event event = template.get(Event.class, id);
		assertNotNullable(event);
		return event;
	}

	@Override
	public Event getSpecificEventOfUser(int userId, int eventId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class, "e");
		criteria.createAlias("e.users", "user", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.idEq(eventId));
		criteria.add(Restrictions.eq("user.id", userId));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		List<Event> events = (List<Event>) template.findByCriteria(criteria);

		if (events.isEmpty()) {
			throw new DaoException("This user doesn't have such event");
		}

		return events.get(0);
	}

	@Override
	public List<Event> getEventsByUserId(int userId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class, "event");
		criteria.createAlias("event.users", "user");
		criteria.add(Restrictions.eq("user.id", userId));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		List<Event> events = (List<Event>) template.findByCriteria(criteria);
		assertNotNullable(events);
		return events;
	}

	@Override
	public List<Event> getFutureEventsByUserId(int userId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class, "event");
		criteria.createAlias("event.users", "user");
		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.gt("event.start", LocalDateTime.now()));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		List<Event> events = (List<Event>) template.findByCriteria(criteria);
		assertNotNullable(events);
		return events;
	}

	@Override
	public List<Event> getEventsInRangeByUserId(int userId, LocalDateTime start, LocalDateTime end)
			throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class, "event");
		criteria.createAlias("event.users", "user");
		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.ge("event.start", start));
		criteria.add(Restrictions.le("event.end", end));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		List<Event> events = (List<Event>) template.findByCriteria(criteria);
		assertNotNullable(events);
		return events;
	}

	@Override
	public List<Event> getEventsInRange(LocalDateTime start, LocalDateTime end) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class, "event");
		criteria.add(Restrictions.ge("event.start", start));
		criteria.add(Restrictions.le("event.end", end));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		List<Event> events = (List<Event>) template.findByCriteria(criteria);
		assertNotNullable(events);
		return events;
	}

	@Override
	public List<Event> getFutureEventsByUserIdMinutesAndHours(int userId, int minutes, int hours) throws DaoException {
		LocalDateTime futureTime = LocalDateTime.now().plusHours(hours).plusMinutes(minutes);

		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class, "event");
		criteria.createAlias("event.users", "user");
		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.between("event.start", futureTime, futureTime.plusMinutes(1)));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		List<Event> events = (List<Event>) template.findByCriteria(criteria);
		assertNotNullable(events);
		return events;
	}

	/**
	 * POST operations
	 * 
	 */

	@Override
	public void addEvent(Event event) throws DaoException {
		template.persist(event);
	}

	@Override
	public void addEvents(List<Event> events) throws DaoException {
		for (Event event : events) {
			template.persist(event);
		}
	}

	/**
	 * PUT operations
	 * 
	 */

	@Override
	public void updateEvent(Event event) throws DaoException {
		template.merge(event);
	}

	@Override
	public void updateEvents(List<Event> events) throws DaoException {
		for (Event event : events) {
			template.merge(event);
		}
	}

	/**
	 * DELETE operations
	 * 
	 */

	@Override
	public void deleteEvent(Event event) throws DaoException {
		event.setActive(false);
		template.merge(event);
	}

	@Override
	public void deleteEvents(List<Event> events) throws DaoException {
		for (Event event : events) {
			event.setActive(false);
			template.merge(event);
		}
	}

	@Override
	public void hardDeleteEvent(Event event) throws DaoException {
		template.delete(event);

	}

	@Override
	public void hardDeleteEvents(List<Event> events) throws DaoException {
		for (Event event : events) {
			template.delete(event);
		}
	}
}
