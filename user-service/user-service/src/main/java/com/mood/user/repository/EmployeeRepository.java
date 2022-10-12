package com.mood.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mood.user.entity.AppUser;
import com.mood.user.entity.Employee;
import com.mood.user.entity.AppUser.UserStatus;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
	Employee findByEmail(String email);
	@Modifying(clearAutomatically = true)
	@Query("update Employee u set u.failedLogins = :failedLogins where u.email = :email")
	@Transactional
	int incrementFailedLogins(@Param("email") String email, @Param("failedLogins") int failedLogins);

	@Modifying(clearAutomatically = true)
	@Query("update Employee u set u.failedLogins = 0 where u.email = :email")
	@Transactional
	void unlockAccount(@Param("email") String email);
	
	@Modifying(clearAutomatically = true)
	@Query("update Employee u set u.failedLogins = 0 where u.email = :email")
	@Transactional
	void unlockEmployeeAccount(@Param("email") String email);

	
	@Modifying(clearAutomatically = true)
	@Query(value="SELECT email, first_name, last_name, access FROM employees where MATCH(first_name, last_name, email, phone_number, access) AGAINST (:keyword)",
			nativeQuery=true)
	@Transactional
	List<Employee> search(@Param("keyword") String keyword);
}
