package com.mood.user.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mood.user.entity.Permission;
import com.mood.user.entity.Role;
import com.mood.user.repository.PermissionRepository;
import com.mood.user.repository.RoleRepository;
import com.mood.user.repository.UserRepository;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
	
	boolean alreadySetup = false;
	
	
    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private RoleRepository roleRepository;
 
    @Autowired
    private PermissionRepository permissionRepository;

    
    private Logger logger = Logger.getLogger(getClass().getName());
    
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup)
            return;
        logger.info("Creating permissions");
        
        Permission readPrivilege
          = createPermissionIfNotFound("READ", "READ_PERMISSION");
        Permission writePrivilege
          = createPermissionIfNotFound("WRITE", "WRITE_PERMISSION");
 
        List<Permission> adminPrivileges = Arrays.asList(
          readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_UNPAID", Arrays.asList(readPrivilege));
        createRoleIfNotFound("ROLE_GOLD", Arrays.asList(readPrivilege));
        createRoleIfNotFound("ROLE_DIAMOND", Arrays.asList(readPrivilege));
        
        alreadySetup = true;
	}

	@Transactional
	private Role createRoleIfNotFound(String name, Collection<Permission> permissions) {
		
		Role role = roleRepository.findByName(name);
		if (role == null) {
            role = new Role(name);
            role.setPermissions(permissions);
            roleRepository.save(role);
        }
		return role;
		
	}
	
	
	@Transactional
	private Permission createPermissionIfNotFound(String key, String name) {
		Permission permission = permissionRepository.findByName(name);
		if (permission == null) {
			permission = new Permission(name, key);
			permissionRepository.save(permission);
		}
		return permission;
	}
}
