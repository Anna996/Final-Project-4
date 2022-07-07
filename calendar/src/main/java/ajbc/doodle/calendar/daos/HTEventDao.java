package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.Event;

@SuppressWarnings("unchecked")
@Repository("HTEventDao")
public class HTEventDao implements EventDao {

	@Autowired
	private HibernateTemplate template;

	@Override
	public List<Event> getAllEvents() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Event> events = (List<Event>) template.findByCriteria(criteria);

		if (events.isEmpty()) {
			throw new DaoException("There are no events in DB");
		}

		return events;
	}

	// TODO getEventsByUserId
	@Override
	public List<Event> getEventsByUserId(int id) throws DaoException {

		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class);
		criteria.add(Restrictions.eqOrIsNull("", id));

		List<Event> events = null;
		return null;
	}

	@Override
	public Event getEventById(int id) throws DaoException {
		Event event = template.get(Event.class, id);

		if (event == null) {
			throw new DaoException("There is no such event in DB");
		}

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
	public void addEvent(Event event) throws DaoException {
		try {
			template.persist(event);
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
	}

	@Override
	public void updateEvent(Event event) throws DaoException {
		try {
			template.merge(event);
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
	}
}
