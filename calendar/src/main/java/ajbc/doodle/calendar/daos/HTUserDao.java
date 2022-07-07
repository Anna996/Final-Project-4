package ajbc.doodle.calendar.daos;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.User;

@SuppressWarnings("unchecked")
@Repository("HTUserDao")
public class HTUserDao implements UserDao {

	@Autowired
	private HibernateTemplate template;

	@Override
	public List<User> getAllUsers() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class,"u");
		criteria.createAlias("u.events", "event", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("event.notifications", "n", JoinType.LEFT_OUTER_JOIN);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<User> users = (List<User>) template.findByCriteria(criteria);

		if (users.isEmpty()) {
			throw new DaoException("There are no users in DB");
		}

		return users;
	}

	@Override
	public User getUserById(int id) throws DaoException {
		User user = template.get(User.class, id);

		if (user == null) {
			throw new DaoException("There is no such user in DB");
		}

		return user;
	}

	@Override
	public void addUser(User user) throws DaoException {
		try {
			template.persist(user);
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
	}

	@Override
	public void updateUser(User user) throws DaoException {
		try {
			template.merge(user);
		} catch (Exception e) {
			throw new DaoException(e.getMessage());
		}
	}
	
}
