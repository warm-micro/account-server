package com.example.account.service;

import java.util.ArrayList;

import com.example.account.model.UserEntity;
import com.example.account.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService{
    @Autowired
    UserRepository userRepository;

    @Override    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if("zzzinho".equals(username)) {
            return new User("zzzinho","$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6", new ArrayList<>());
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    public UserDetails saveUser(String username, String password, String nickname) throws Exception {
        userRepository.save(new UserEntity(username,password,nickname));
        return new User(username, password, new ArrayList<>());
    }
}
