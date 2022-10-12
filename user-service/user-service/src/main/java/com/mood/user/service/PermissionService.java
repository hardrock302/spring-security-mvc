package com.mood.user.service;

import org.springframework.http.ResponseEntity;

import com.mood.user.entity.Permission;

public interface PermissionService {
	public ResponseEntity<String> savePermission();
	public ResponseEntity<String> updatePermission();
	public ResponseEntity<String> deletePermission();
	public Permission getPermission();
}
