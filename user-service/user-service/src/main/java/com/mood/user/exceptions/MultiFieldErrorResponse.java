package com.mood.user.exceptions;


import java.util.HashMap;
import java.util.List;

import org.springframework.validation.FieldError;
public class MultiFieldErrorResponse  extends ErrorResponse{

	
	private HashMap<String, String> fieldErrors;

	
	public MultiFieldErrorResponse(int status, List<FieldError> fieldErrors, long timestamp) {
		this.status = status;
		this.fieldErrors = new HashMap<String, String>();
		fieldErrors.forEach(e -> this.fieldErrors.put(e.getField(), e.getDefaultMessage()));
		this.timestamp = timestamp;
		this.message = "Please correct the following fields.";

	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public HashMap<String, String> getFieldErrors() {
			return fieldErrors;
	}
	public void setFieldErrors(List<FieldError> fieldErrors) {
		fieldErrors.forEach(e -> this.fieldErrors.put(e.getField(), e.getDefaultMessage()));
		
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	

	
	
}
