package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.SubscriptionData;

@Repository
@Transactional(readOnly = true, rollbackFor = {DaoException.class})
public interface SubscriptionDataDao {

	List<SubscriptionData> getAllSubscriptions() throws DaoException;
	
	SubscriptionData getSubscriptionByEndPoint(String endPoint) throws DaoException;
	
	@Transactional(readOnly = false)
	void addSubscription(SubscriptionData subscription) throws DaoException;
	
	@Transactional(readOnly = false)
	void deleteSubscription(SubscriptionData subscription) throws DaoException;
}
