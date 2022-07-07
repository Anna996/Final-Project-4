//package ajbc.doodle.calendar.daos;
//
//import java.util.List;
//
//import org.hibernate.criterion.DetachedCriteria;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.orm.hibernate5.HibernateTemplate;
//import org.springframework.stereotype.Repository;
//
//import ajbc.doodle.calendar.entities.UserEvent;
//import ajbc.doodle.calendar.entities.UserEventPK;
//
//@SuppressWarnings("unchecked")
//@Repository("HTUserEventDao")
//public class HTUserEventDao implements UserEventDao {
//
//	@Autowired
//	HibernateTemplate template;
//	
//	@Override
//	public List<UserEvent> getAllUserEvents() throws DaoException{
//		DetachedCriteria criteria = DetachedCriteria.forClass(UserEvent.class);
//		List<UserEvent> usersEvents = (List<UserEvent>) template.findByCriteria(criteria);
//	
//		if (usersEvents.isEmpty()) {
//			throw new DaoException("There are no user-events in DB");
//		}
//
//		return usersEvents;
//	}
//	
//	@Override
//	public UserEvent getUserEventByIDs(int userId, int eventId) throws DaoException {
//		
//		UserEvent userEvent = template.get(UserEvent.class, new UserEventPK(userId, eventId));
//		
//		if(userEvent == null) {
//			throw new DaoException("There are no such user-event in DB");
//		}
//		
//		return userEvent;
//	}
//
//	@Override
//	public void addUserEvent(UserEvent userEvent) throws DaoException {
//		try {
//			template.persist(userEvent);
//		}
//		catch (Exception e) {
//			throw new DaoException(e.getMessage());
//		}
//	}
//
//
//}
