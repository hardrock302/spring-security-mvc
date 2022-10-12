package com.mood.user.service;





import javax.validation.Valid;

import org.springframework.http.ResponseEntity;

import com.mood.user.entity.AppUser;
import com.mood.user.entity.AppUser.UserStatus;
import com.mood.user.entity.Employee;



public interface UserService {
	public ResponseEntity<String> saveUser(AppUser user);
	public ResponseEntity<String> updateUser(String oldEmail, AppUser user);
	public ResponseEntity<String> deleteUser(AppUser user);
	public AppUser getUser(AppUser user);
	public AppUser getUser(String email);
	public Employee getEmployee(String user);
	public ResponseEntity<String> exists (AppUser user);
	public Employee getEmployee(Employee user);
	public ResponseEntity<String> updateEmployee(String oldEmail, Employee employee);
	public ResponseEntity<String> saveEmployee(@Valid Employee employee);
	public ResponseEntity<String> deleteEmployee(Employee employee);
	public ResponseEntity<String> unlockUserAccount(String user);
	public ResponseEntity<String> unlockEmployeeAccount(String user);
	public ResponseEntity<String> setUserAccountStatus(String user, UserStatus status);
	public ResponseEntity<String> setAdminAccountStatus(String user, UserStatus status);
	public ResponseEntity<String> changeUserPassword(AppUser user);
	public ResponseEntity<String> changeEmployeePassword(Employee employee);
	public ResponseEntity<String> employeeSearch(String keyword);
	public ResponseEntity<String> userSearch(String keyword);
	public ResponseEntity<String> banAccount(String user);


}
	


