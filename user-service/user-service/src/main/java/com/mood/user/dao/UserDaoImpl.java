package com.mood.user.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Base64Utils;

import com.mood.user.entity.AppUser;
import com.mood.user.entity.AppUser.UserStatus;
import com.mood.user.entity.Employee;
import com.mood.user.entity.Role;
import com.mood.user.exceptions.UserNotFoundException;
import com.mood.user.repository.RoleRepository;
import com.mood.user.repository.UserRepository;




@Repository 
public class UserDaoImpl implements UserDao{
	
	@PersistenceContext
	private EntityManager entityManager;
	

	private Logger logger = Logger.getLogger(getClass().getName());

	
	@Override
	public AppUser getUserWithId(int id) {
		List<AppUser> results = getEntityWithPredicate(AppUser.class, "id", id);
		if (results.size() != 1) {
			throw new UserNotFoundException();
		} else {
			return results.get(0);
		}
	}

	@Override
	public AppUser getUserByEmail(String email) {
		List<AppUser> results = getEntityWithPredicate(AppUser.class, "email",email);
		if (results.size() != 1) {
			throw new UserNotFoundException();
		} else {
			return results.get(0);
		}
	}

	@Override
	public AppUser getUserWithPhoneNumber(String phoneNumber) {
		List<AppUser> results = getEntityWithPredicate(AppUser.class, "phoneNumber", phoneNumber);
		if (results.size() != 1) {
			throw new UserNotFoundException();
		} else {
			return results.get(0);
		}
	}
	
	@Override
	public <E, T> int getNumberOfEntityWith(Class<E> classType, String fieldName, T fieldValue) {
		List<E> results = getEntityWithPredicate(classType, fieldName, fieldValue);
		return results.size();
	}
	
	@Override
	public <T> boolean saveEntity(T user) {
		Session session = entityManager.unwrap(Session.class);
		try {
			session.persist(user);
			return true;
		} catch(Exception e) {
			logger.error(e);
			return false;
			
		}
		
	}
	
	
	
	@Override
	public <E> boolean deleteEntity(Class<E> classType, String email) {
		try {
			Session session = entityManager.unwrap(Session.class);
			CriteriaBuilder criteria = session.getCriteriaBuilder();
			CriteriaDelete<E> criteriaDelete = criteria.createCriteriaDelete(classType);
			Root<E> root = criteriaDelete.from(classType);
			criteriaDelete.where(criteria.equal(root.get("email"), email));
			int rows = session.createQuery(criteriaDelete).executeUpdate();
			if (rows == 1)
				return true;
			else 
				return false;
		}catch(Exception e) {
			logger.error(e);
			return false;
		
		}
		
	}
	
	
	private <E, T> List<E> getEntityWithPredicate(Class<E> classType, String fieldName, T fieldValue) {
		Session session = entityManager.unwrap(Session.class);
		try {
			CriteriaBuilder criteria = session.getCriteriaBuilder();
			CriteriaQuery<E> cq = criteria.createQuery(classType);
			Root<E> root = cq.from(classType);
			Predicate predicate = criteria.equal(root.get(fieldName), fieldValue);
			List<E> entities;
			Query<E> query = session.createQuery(cq.select(root).where(predicate));
			entities = query.getResultList();
			return entities;
		} catch (NoResultException exc) {
			throw new UserNotFoundException("An user with " + fieldName + ": " + fieldValue + " does not exist.");
		}

	}
	
	@Override
	public boolean updateUser(String oldEmail, AppUser user) {
		// TODO Auto-generated method stub
		Session session = entityManager.unwrap(Session.class);
		try {
			List<AppUser> users = getEntityWithPredicate(AppUser.class, "email", oldEmail);
			AppUser currentUserData = users.get(0);
			user.setId(currentUserData.getId());
			CriteriaBuilder criteria = session.getCriteriaBuilder();
			CriteriaUpdate<AppUser> criteriaUpdate = criteria.createCriteriaUpdate(AppUser.class);
			Root<AppUser> root = criteriaUpdate.from(AppUser.class);
			criteriaUpdate.where(criteria.equal(root.get("id"), currentUserData.getId()));
			criteriaUpdate.set("email", user.getEmail());
			criteriaUpdate.set("phoneNumber", user.getPhoneNumber());
			criteriaUpdate.set("firstName", user.getFirstName());
			criteriaUpdate.set("lastName", user.getLastName());
			criteriaUpdate.set("membershipLevel", user.getMembershipLevel());
			criteriaUpdate.set("membershipExpiryDate", user.getMembershipExpiryDate());
			int rows = session.createQuery(criteriaUpdate).executeUpdate();
			if (rows == 1)
				return true;
			else
				return false;
		} catch(Exception e) {
			logger.error(e);
			return false;
			
		}
	}

	
	@Override
	public Employee getEmployeeByEmail(String email) {

		List<Employee> results = getEntityWithPredicate(Employee.class, "email",email);
		if (results.size() != 1) {
			throw new UserNotFoundException();
		} else {
			return results.get(0);
		}
	}

	@Override
	public boolean updateEmployee(String oldEmail, Employee employee) {
		Session session = entityManager.unwrap(Session.class);
		try {
			List<Employee> results = getEntityWithPredicate(Employee.class, "email", oldEmail);
			Employee currentEmployeeData = results.get(0);
			employee.setId(currentEmployeeData.getId());
			CriteriaBuilder criteria = session.getCriteriaBuilder();
			CriteriaUpdate<Employee> criteriaUpdate = criteria.createCriteriaUpdate(Employee.class);
			Root<Employee> root = criteriaUpdate.from(Employee.class);
			criteriaUpdate.where(criteria.equal(root.get("id"), currentEmployeeData.getId()));
			criteriaUpdate.set("email", employee.getEmail());
			criteriaUpdate.set("phoneNumber", employee.getPhoneNumber());
			criteriaUpdate.set("firstName", employee.getFirstName());
			criteriaUpdate.set("lastName", employee.getLastName());
			criteriaUpdate.set("access", employee.getAccess());
			int rows = session.createQuery(criteriaUpdate).executeUpdate();
			if (rows == 1)
				return true;
			else
				return false;
		} catch(Exception e) {
			logger.error(e);
			return false;
			
		}
	}
	
	@Override
	public <T> boolean updateEntityPassword(String email, String encodedPassword, Class<T> classType) {
		Session session = entityManager.unwrap(Session.class);
		try {
			CriteriaBuilder criteria = session.getCriteriaBuilder();
			CriteriaUpdate<T> criteriaUpdate = criteria.createCriteriaUpdate(classType);
			Root<T> root = criteriaUpdate.from(classType);
			criteriaUpdate.where(criteria.equal(root.get("email"), email));
			criteriaUpdate.set("password", encodedPassword);
			int rows = session.createQuery(criteriaUpdate).executeUpdate();
			if (rows == 1)
				return true;
			else
				return false;
		} catch(Exception e) {
			logger.error(e);
			return false;
			
		}
	}

	public <T> boolean updateEntityStatus(String email, UserStatus status, Class<T> classType) {
		Session session = entityManager.unwrap(Session.class);
		try {
			CriteriaBuilder criteria = session.getCriteriaBuilder();
			CriteriaUpdate<T> criteriaUpdate = criteria.createCriteriaUpdate(classType);
			Root<T> root = criteriaUpdate.from(classType);
			criteriaUpdate.where(criteria.equal(root.get("email"), email));
			criteriaUpdate.set("enabled", status);
			int rows = session.createQuery(criteriaUpdate).executeUpdate();
			if (rows == 1)
				return true;
			else
				return false;
		} catch(Exception e) {
			logger.error(e);
			return false;
			
		}
	}
	@Override
	public Employee getEmployeeWithId(int id) {
		List<Employee> results = getEntityWithPredicate(Employee.class, "id",id);
		if (results.size() != 1) {
			throw new UserNotFoundException();
		}
		if (results.size() != 1) {
			throw new UserNotFoundException();
		} else {
			return results.get(0);
		}
	}

	@Override
	public Employee getEmployeeWithPhoneNumber(String phoneNumber) {
		List<Employee> results = getEntityWithPredicate(Employee.class, "phoneNumber", phoneNumber);
		if (results.size() != 1) {
			throw new UserNotFoundException();
		} else {
			return results.get(0);
		}
	}

	@Override
	public <E> void invalidLogin(String email, Class<E> classType) {
		// TODO Auto-generated method stub
		List<E> results = getEntityWithPredicate(classType, "email", email);
		if (results.size() != 1) {
			throw new UserNotFoundException();
		} else {
			E object = results.get(0);
			if (object instanceof Employee) {
				int logins = ((Employee) object).getFailedLogins();
				((Employee) object).setFailedLogins(logins+1);
				boolean success = updateEmployee(email, (Employee)object);
			} else if (object instanceof AppUser) {
				int logins = ((AppUser) object).getFailedLogins();
				((AppUser) object).setFailedLogins(logins+1);
				boolean success = updateUser(email, (AppUser)object);
			}
		}
	}

	@Override
	public <T> T[] search(String[] parameters, Class<T> classType) {
		
		return null;
	}





	
}
