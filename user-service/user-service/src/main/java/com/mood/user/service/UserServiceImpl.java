package com.mood.user.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.catalina.User;
import org.jboss.logging.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mood.user.dao.UserDao;
import com.mood.user.entity.AppUser;
import com.mood.user.entity.AppUser.UserStatus;
import com.mood.user.entity.Constants;
import com.mood.user.entity.Employee;
import com.mood.user.entity.Permission;
import com.mood.user.entity.Role;
import com.mood.user.exceptions.UserNotFoundException;
import com.mood.user.exceptions.UserUpdateException;
import com.mood.user.repository.EmployeeRepository;
import com.mood.user.repository.RoleRepository;
import com.mood.user.repository.UserRepository;
import com.mood.user.security.SecurityConfig;


@Service("userService")
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserDao userDao;

	private JSONObject obj;
	
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	

	private Logger logger = Logger.getLogger(getClass().getName());
	
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_UNPAID')")
	public AppUser getUser(AppUser user) {
		String email = user.getEmail();
	    AppUser result = userDao.getUserByEmail(email);
	    if (result != null) {
	    	AppUser response = new AppUser(result.getEmail(), result.getFirstName(), result.getLastName(),
	    		result.getEnabled(), result.getMembershipLevel(), result.getMembershipExpiryDate());
	    	return response;
	    } else {
	    	throw new UserNotFoundException("User not found " + email);
	    }
	}

	@Override
	@PreAuthorize("hasRole('ROLE_UNPAID')")
	@Transactional
	public AppUser getUser(String email) {
	    AppUser result = userDao.getUserByEmail(email);
	    if (result != null) {
	    	AppUser response = new AppUser(result.getEmail(), result.getFirstName(), result.getLastName(),
	    		result.getEnabled(), result.getMembershipLevel(), result.getMembershipExpiryDate());
	    	return response;
	    } else {
	    	throw new UserNotFoundException("User not found " + email);
	    }
	}

	@Override
	@Transactional
	public ResponseEntity<String> saveUser(AppUser user) {
		String unencryptedPassword = SecurityConfig.decodeBase64Password(user.getPassword());
		String encodedPassword = passwordEncoder.encode(unencryptedPassword);
		user.setPassword(encodedPassword);
		Collection<Role> roles = getRoles(user.getMembershipLevel());
		user.setRoles(roles);
		int existingEmail = 0;
		int existingPhone = 0;
		existingEmail = userDao.getNumberOfEntityWith(AppUser.class, "email", user.getEmail());
		existingPhone = userDao.getNumberOfEntityWith(AppUser.class, "phoneNumber", user.getPhoneNumber());
		if (existingEmail > 0 && existingPhone > 0) {
			throw new UserUpdateException(user.getEmail() + " and " + user.getPhoneNumber() + " are already in use.");
		}
		else if (existingEmail > 0) {
			throw new UserUpdateException(user.getEmail() + " is already in use.");
		} else if (existingPhone > 0) {
			throw new UserUpdateException(user.getPhoneNumber() + " is already in use.");
		}
		//set enabled to zero always
		user.setEnabled(UserStatus.INACTIVE);
		boolean result = userDao.saveEntity(user);
	    if (result) {
	    	obj = new JSONObject();
	    	obj.put("status-code", HttpStatus.CREATED.value());
	    	obj.put("message", user.getEmail() + " saved.");
	    	return new ResponseEntity<String>(obj.toString(), HttpStatus.CREATED);
	    }else {
	    	throw new UserUpdateException(user.getEmail() + " creation failed.");
	    }
	}


	
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> deleteUser(AppUser user) {
		String email = user.getEmail();
		if (!isUserBanned(email)) {
			throw new UserUpdateException("User not found " + user.getEmail());
		}
	    boolean result = userDao.deleteEntity(AppUser.class, email);
	    if (result) {
	    	obj = new JSONObject();
	    	obj.put("status-code", HttpStatus.NO_CONTENT.value());
	    	return new ResponseEntity<String>(obj.toString(), HttpStatus.NO_CONTENT);
	    } else {
	    	throw new UserNotFoundException("User not found " + email);
	    }
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_UNPAID')")
	public ResponseEntity<String> updateUser(String oldEmail, AppUser user) {
		if (!isUserBanned(oldEmail)) {
			throw new UserUpdateException("User not found " + user.getEmail());
		}
		Collection<Role> roles = getRoles(user.getMembershipLevel());
		user.setRoles(roles);
	    boolean result = userDao.updateUser(oldEmail, user);
	    if (result) {
	    	obj = new JSONObject();
	    	obj.put("status-code", HttpStatus.OK.value());
	    	obj.put("message", user.getEmail() + " updated.");
	    	return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
	    } else {
	    	throw new UserUpdateException("Employee not found " + user.getEmail());
	    }	
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Employee getEmployee(Employee employee) {
		String email = employee.getEmail();
	    Employee result = userDao.getEmployeeByEmail(email);
	    if (result != null) {
	    	Employee response = new Employee(result.getEmail(), result.getFirstName(), result.getLastName(), result.getAccess());
	    	return response;
	    } else {
	    	throw new UserNotFoundException("Employee not found " + email);
	    }
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Employee getEmployee(String email) {
	    Employee result = userDao.getEmployeeByEmail(email);
	    if (result != null) {
	    	Employee response = new Employee(result.getEmail(), result.getFirstName(), result.getLastName(), result.getAccess());
	    	return response;
	    } else {
	    	throw new UserNotFoundException("Employee not found " + email);
	    }
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> saveEmployee(Employee employee) {
		String unencryptedPassword = SecurityConfig.decodeBase64Password(employee.getPassword());
		String encodedPassword = passwordEncoder.encode(unencryptedPassword);
		employee.setPassword(encodedPassword);
		Collection<Role> roles = getRoles(employee.getAccess());
		employee.setRoles(roles);
		int existingEmail = 0;
		int existingPhone = 0;
		existingEmail = userDao.getNumberOfEntityWith(Employee.class, "email", employee.getEmail());
		existingPhone = userDao.getNumberOfEntityWith(Employee.class, "phoneNumber", employee.getPhoneNumber());
		if (existingEmail > 0 && existingPhone > 0) {
			throw new UserUpdateException(employee.getEmail() + " and " + employee.getPhoneNumber() + " are already in use.");
		}
		else if (existingEmail > 0) {
			throw new UserUpdateException(employee.getEmail() + " is already in use.");
		} else if (existingPhone > 0) {
			throw new UserUpdateException(employee.getPhoneNumber() + " is already in use.");
		}
		//set enabled to zero always
		employee.setEnabled(UserStatus.INACTIVE);
		boolean result = userDao.saveEntity(employee);
	    if (result) {
	    	obj = new JSONObject();
	    	obj.put("status-code", HttpStatus.CREATED.value());
	    	obj.put("message", employee.getEmail() + " saved.");
	    	return new ResponseEntity<String>(obj.toString(), HttpStatus.CREATED);
	    }else {
	    	throw new UserUpdateException(employee.getEmail() + " creation failed.");
	    }
	}


	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> deleteEmployee(Employee employee) {
		String email = employee.getEmail();
		if (!isUserBanned(email)) {
			throw new UserUpdateException("User not found " + employee.getEmail());
		}
	    boolean result = userDao.deleteEntity(Employee.class, email);
	    if (result) {
	    	obj = new JSONObject();
	    	obj.put("status-code", HttpStatus.NO_CONTENT.value());
	    	return new ResponseEntity<String>(obj.toString(), HttpStatus.NO_CONTENT);
	    } else {
	    	throw new UserNotFoundException("User not found " + email);
	    }
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> updateEmployee(String oldEmail, Employee employee) {
		if (!isUserBanned(oldEmail)) {
			throw new UserUpdateException("User not found " + employee.getEmail());
		}
		Collection<Role> roles = getRoles(employee.getAccess());
		employee.setRoles(roles);
	    boolean result = userDao.updateEmployee(oldEmail, employee);
	    if (result) {
	    	obj = new JSONObject();
	    	obj.put("status-code", HttpStatus.OK.value());
	    	obj.put("message", employee.getEmail() + " updated.");
	    	return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
	    } else {
	    	throw new UserUpdateException("User not found " + employee.getEmail());
	    }	
	}
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> exists(AppUser user) {
		String email = user.getEmail();
	    AppUser result = userDao.getUserByEmail(email);
	    if (result != null) {
	    	obj = new JSONObject();
	    	obj.put("status-code", HttpStatus.OK.value());
	    	obj.put("message", "User exists");
	    	return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
	    } else {
	    	throw new UserNotFoundException("AppUser not found " + email);
	    }
	}
	

	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	private boolean isUserBanned(String email) {
		AppUser user = userDao.getUserByEmail(email);
		if (user.getEnabled() == UserStatus.BANNED) {
			return false;
		}
		return true;
	}

	private List<String> getPermissions(Collection<Role> roles){
		List<String> permissions = new ArrayList<String>();
		List<Permission> collection = new ArrayList<>();
		
		for (Role role: roles) {
			permissions.add(role.getName());
			collection.addAll(role.getPermissions());
		}
		return permissions;
	}
	
	private Collection<Role> getRoles(String membershipLevel){
		Collection<Role> roles = new ArrayList<Role>();
		Role role;
		switch (membershipLevel) {
			case Constants.DIAMOND:
				role = roleRepository.findByName("ROLE_"+Constants.DIAMOND);
				roles.add(role);
				break;
			case Constants.GOLD:
				role = roleRepository.findByName("ROLE_"+Constants.GOLD);
				roles.add(role);
				break;
			case Constants.UNPAID:
				role = roleRepository.findByName("ROLE_"+Constants.UNPAID);
				roles.add(role);
				break;
		
		}
		return roles;
	}
	
	private List<GrantedAuthority> getGrantedAuthorities(List<String> permissions){
		List<GrantedAuthority> authorities = new ArrayList<>();
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
	}
	
    private Collection<? extends GrantedAuthority> getAuthorities(
    	      Collection<Role> roles) {
    	return getGrantedAuthorities(getPermissions(roles));
    }

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.getUser(username);
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> unlockUserAccount(String user) {
		userRepository.unlockAccount(user);
    	obj = new JSONObject();
    	obj.put("status-code", HttpStatus.OK.value());
    	obj.put("message", user + " unlocked.");
		return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> unlockEmployeeAccount(String user) {
		employeeRepository.unlockAccount(user);
    	obj = new JSONObject();
    	obj.put("status-code", HttpStatus.OK.value());
    	obj.put("message", user + " unlocked.");
		return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_UNPAID')")
	public ResponseEntity<String> setUserAccountStatus(String user, UserStatus status) {
		switch (status) {
			case INACTIVE:
				userDao.updateEntityStatus(user, UserStatus.INACTIVE, AppUser.class);
				break;
			case ACTIVE:
				userDao.updateEntityStatus(user, UserStatus.ACTIVE, AppUser.class);
				break;
		}
		obj = new JSONObject();
    	obj.put("status-code", HttpStatus.OK.value());
    	obj.put("message", user + " set to " + status);
		return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> banAccount(String user) {
		userDao.updateEntityStatus(user, UserStatus.BANNED, AppUser.class);
    	obj.put("status-code", HttpStatus.OK.value());
    	obj.put("message", user + " banned.");
		return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
	}
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> setAdminAccountStatus(String user, UserStatus status) {
		switch (status) {
			case INACTIVE:
				userDao.updateEntityStatus(user, UserStatus.INACTIVE, Employee.class);
				break;
			case ACTIVE:
				userDao.updateEntityStatus(user, UserStatus.ACTIVE, Employee.class);
				break;
		}
    	obj = new JSONObject();
    	obj.put("status-code", HttpStatus.OK.value());
    	obj.put("message", user + " set to " + status);
		return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> changeEmployeePassword(Employee employee) {
		String unencryptedPassword = SecurityConfig.decodeBase64Password(employee.getPassword());
		String encodedPassword = passwordEncoder.encode(unencryptedPassword);
		boolean result = userDao.updateEntityPassword(employee.getEmail(), encodedPassword, Employee.class);
	    if (result) {
	    	obj = new JSONObject();
	    	obj.put("status-code", HttpStatus.CREATED.value());
	    	obj.put("message",	employee.getEmail() + " password updated.");
	    	return new ResponseEntity<String>(obj.toString(), HttpStatus.CREATED);
	    }else {
	    	throw new UserUpdateException(employee.getEmail() + " password update failed.");
	    }
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_UNPAID')")
	public ResponseEntity<String> changeUserPassword(AppUser user) {
		String unencryptedPassword = SecurityConfig.decodeBase64Password(user.getPassword());
		String encodedPassword = passwordEncoder.encode(unencryptedPassword);
		boolean result = userDao.updateEntityPassword(user.getEmail(), encodedPassword, AppUser.class);
	    if (result) {
	    	obj = new JSONObject();
	    	obj.put("status-code", HttpStatus.CREATED.value());
	    	obj.put("message",	user.getEmail() + " password updated.");
	    	return new ResponseEntity<String>(obj.toString(), HttpStatus.CREATED);
	    }else {
	    	throw new UserUpdateException(user.getEmail() + " password update failed.");
	    }
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> userSearch(String keyword) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		List<AppUser> search_result = userRepository.search(keyword);
		
		try {
			return new ResponseEntity<String>(objectMapper.writeValueAsString(search_result), HttpStatus.OK);
		} catch (JsonProcessingException e) {
			throw new UserNotFoundException(e.getMessage());
		}
				
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> employeeSearch(String keyword) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		List<Employee> search_result = employeeRepository.search(keyword);
		
		try {
			return new ResponseEntity<String>(objectMapper.writeValueAsString(search_result), HttpStatus.OK);
		} catch (JsonProcessingException e) {
			throw new UserNotFoundException(e.getMessage());
		}
				
	}
}
