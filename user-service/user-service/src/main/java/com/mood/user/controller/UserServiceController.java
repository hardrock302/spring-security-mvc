package com.mood.user.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mood.user.entity.AppUser;
import com.mood.user.entity.Employee;
import com.mood.user.entity.Role;
import com.mood.user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserServiceController {

	@Autowired
	private UserService userService;
	private Logger logger = Logger.getLogger(getClass().getName());
	@GetMapping("/")
	public String hello() {
		return "Hello World! Time on Server is " + LocalDateTime.now();
	}
	
	
	@GetMapping(value="/exists/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> getUser(@RequestBody AppUser user) {
		ResponseEntity<String> result = userService.exists(user);
		return result;
		
	}
	
	@GetMapping(value="/get/", consumes = "application/json", produces = "application/json")
	public AppUser getName(@RequestBody AppUser user) {
		AppUser result = userService.getUser(user.getEmail());
		return result;
		
	}
	
	@PostMapping(value ="/register", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> addUser(@RequestBody @Valid AppUser user) {
		ResponseEntity<String> response = userService.saveUser(user);
		return response;
	}
	
	@PutMapping(value ="/update/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> updateUser(@RequestBody String json) throws JsonParseException, JsonMappingException, IOException {
		JSONObject obj = new JSONObject(json);
		String oldEmail = obj.getString("oldEmail");
		obj.remove("oldEmail");
		ObjectMapper objectMapper = new ObjectMapper();
		AppUser user = objectMapper.readValue(obj.toString(), AppUser.class);	
		ResponseEntity<String> response = userService.updateUser(oldEmail, user);
		return response;
	}

	@DeleteMapping(value="/delete/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> deleteUser(@RequestBody AppUser user) {
		ResponseEntity<String> response = userService.deleteUser(user);
		return response;
	}
	
	@GetMapping(value="/admin/get/", consumes = "application/json", produces = "application/json")
	public Employee getName(@RequestBody Employee user) {
		Employee result = userService.getEmployee(user);
		return result;
		
	}
	@PostMapping(value ="/admin/add/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> addUser(@RequestBody @Valid Employee user) {
		ResponseEntity<String> response = userService.saveEmployee(user);
		return response;
	}
	
	@PutMapping(value ="/admin/update/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> updateEmployee(@RequestBody String json) throws JsonParseException, JsonMappingException, IOException {
		JSONObject obj = new JSONObject(json);

		try {
			String oldEmail = obj.getString("oldEmail");
			obj.remove("oldEmail");
			ObjectMapper objectMapper = new ObjectMapper();
			Employee user = objectMapper.readValue(obj.toString(), Employee.class);	
			ResponseEntity<String> response = userService.updateEmployee(oldEmail, user);
			return response;
		} catch (JSONException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(obj.toString(), HttpStatus.BAD_REQUEST);
		}

		
	}

	@DeleteMapping(value="/admin/delete/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> deleteUser(@RequestBody Employee user) {
		ResponseEntity<String> response = userService.deleteEmployee(user);
		return response;
	}
	
	@PostMapping(value="/admin/user-unlock/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> unlockUserAccount(@RequestBody AppUser user) {
		ResponseEntity<String> response = userService.unlockUserAccount(user.getEmail());
		return response;
		
	}
	
	@PostMapping(value="/admin/employee-unlock/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> unlockEmployeeAccount(@RequestBody Employee user) {
		ResponseEntity<String> response = userService.unlockEmployeeAccount(user.getEmail());
		return response;
		
	}
	
	@PostMapping(value="/password/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> updatePassword(@RequestBody AppUser user) {
		ResponseEntity<String> response = userService.changeUserPassword(user);
		return response;
		
	}
	
	@PostMapping(value="/admin/password/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> updatePassword(@RequestBody Employee user) {
		ResponseEntity<String> response = userService.changeEmployeePassword(user);
		return response;
		
	}
	
	@PostMapping(value="/set-account-status/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> updateAccountStatus(@RequestBody AppUser user) {
		ResponseEntity<String> response = userService.setUserAccountStatus(user.getEmail(), user.getEnabled());
		return response;
		
	}
	
	@PostMapping(value="/ban/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> banAccount(@RequestBody AppUser user) {
		ResponseEntity<String> response = userService.banAccount(user.getEmail());
		return response;
		
	}
	@PostMapping(value="/admin/set-account-status/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> updateAdminAccountStatus(@RequestBody Employee user) {
		ResponseEntity<String> response = userService.setAdminAccountStatus(user.getEmail(), user.getEnabled());
		return response;
		
	}
	
	@PostMapping(value="/search/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> userSearch(@RequestBody String keyword) {
		ResponseEntity<String> response = userService.userSearch(keyword);
		return response;
	}
	
	@PostMapping(value="/admin/search/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> employeeSearch(@RequestBody String keyword) {
		ResponseEntity<String> response = userService.employeeSearch(keyword);
		return response;
	}
}
