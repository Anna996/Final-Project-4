package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.SubscriptionDataDao;
import ajbc.doodle.calendar.entities.SubscriptionData;

@Service
public class SubscriptionDataService {
	
	@Autowired
	@Qualifier("HTSubscriptionDataDao")
	private SubscriptionDataDao dataDao;
	
	
	public List<SubscriptionData> getAllSubscriptions() throws DaoException {
		return dataDao.getAllSubscriptions();
	}

	public void addSubscription(SubscriptionData subscription) throws DaoException {
		if(subscriptionExists(subscription)) {
			throw new DaoException("End point already exists");
		}
		
		dataDao.addSubscription(subscription);
	}

	public void deleteUserSubscription(String endPoint) throws DaoException {
		SubscriptionData subscription = dataDao.getSubscriptionByEndPoint(endPoint);
		dataDao.deleteSubscription(subscription);
	}
	
	private boolean subscriptionExists(SubscriptionData subscription) {
		try {
			dataDao.getSubscriptionByEndPoint(subscription.getEndPoint());
			return true;
		} catch (DaoException e) {
			return false;
		}
	}
}
