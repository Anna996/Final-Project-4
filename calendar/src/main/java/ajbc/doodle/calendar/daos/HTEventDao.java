package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
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
		List<Event> events = (List<Event>) template.findByCriteria(criteria);
	
		if (events.isEmpty()) {
			throw new DaoException("There are no events in DB");
		}

		return events;
	}
	
	@Override
	public Event getEventById(int id) throws DaoException {
		Event event = template.get(Event.class, id);
		
		if(event == null) {
			throw new DaoException("There are no such event in DB");
		}
		
		return event;
	}

	@Override
	public void addEvent(Event event) throws DaoException {
		try {
			template.persist(event);
		}
		catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
	}

	

}
