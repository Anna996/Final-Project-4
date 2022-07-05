package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.UserEvent;

@SuppressWarnings("unchecked")
@Repository("HTUserEventDao")
public class HTUserEventDao implements UserEventDao {

	@Autowired
	HibernateTemplate template;
	
	@Override
	public List<UserEvent> getAllUserEvents() throws DaoException{
		DetachedCriteria criteria = DetachedCriteria.forClass(UserEvent.class);
		return (List<UserEvent>) template.findByCriteria(criteria);
	}
	
	
}
