package com.mood.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mood.user.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>{
	Permission findByName(String name);
}