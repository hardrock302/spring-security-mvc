package com.mood.user.dao;


import org.springframework.http.ResponseEntity;

import com.mood.user.entity.AppUser;
import com.mood.user.entity.AppUser.UserStatus;
import com.mood.user.entity.Employee;


public interface UserDao {

	public AppUser getUserByEmail(String email);
	public boolean updateUser(String oldEmail, AppUser user);
	public AppUser getUserWithId(int id);
	public AppUser getUserWithPhoneNumber(String phoneNumber);
	public Employee getEmployeeByEmail(String email);
	public boolean updateEmployee(String oldEmail, Employee employee);
	public Employee getEmployeeWithId(int id);
	public Employee getEmployeeWithPhoneNumber(String phoneNumber);
	public <E> boolean deleteEntity(Class<E> classType, String email);
	public <T> boolean saveEntity(T user);
	public <E, T> int getNumberOfEntityWith(Class<E> classType, String fieldName, T fieldValue);
	public <E> void invalidLogin(String email, Class<E> classType);
	public <T> T[] search(String[] parameters, Class<T> classType);
	public <T> boolean updateEntityPassword(String email, String encodedPassword, Class<T> classType);
	public <T> boolean updateEntityStatus(String user, UserStatus status, Class<T> classType);


}
