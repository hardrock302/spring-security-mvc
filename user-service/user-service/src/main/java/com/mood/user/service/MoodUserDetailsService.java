package com.mood.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mood.user.entity.AppUser;
import com.mood.user.repository.UserRepository;

@Service
public class MoodUserDetailsService implements UserDetailsService{

    @Autowired
    private UserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser user = userService.getUser(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}
