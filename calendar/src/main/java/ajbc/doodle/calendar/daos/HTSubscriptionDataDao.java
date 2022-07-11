package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.SubscriptionData;

@SuppressWarnings("unchecked")
@Repository("HTSubscriptionDataDao")
public class HTSubscriptionDataDao implements SubscriptionDataDao {

	@Autowired
	private HibernateTemplate template;

	@Override
	public List<SubscriptionData> getAllSubscriptions() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(SubscriptionData.class);
		return (List<SubscriptionData>) template.findByCriteria(criteria);
	}

	@Override
	public SubscriptionData getSubscriptionByEndPoint(String endPoint) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(SubscriptionData.class);
		criteria.add(Restrictions.idEq(endPoint));
		
		List<SubscriptionData> subscriptions = (List<SubscriptionData>)template.findByCriteria(criteria);

		if (subscriptions.isEmpty()) {
			throw new DaoException("There is no such subscription in DB");
		}

		return subscriptions.get(0);
	}

	@Override
	public void addSubscription(SubscriptionData subscription) throws DaoException {
		template.persist(subscription);
	}

	@Override
	public void deleteSubscription(SubscriptionData subscription) throws DaoException {
		template.delete(subscription);
	}
}
