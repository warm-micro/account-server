package com.example.account.service;

import java.util.ArrayList;
import java.util.Optional;

import com.example.account.model.UserEntity;
import com.example.account.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> uOptional = userRepository.findByUsername(username);
        if (uOptional.isPresent()) {
            return new User(uOptional.get().getUsername(), uOptional.get().getPassword(), new ArrayList<>());        
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    public UserDetails loadUserByUsernameAndPassword(String username, String password) throws Exception {
        String encodedPassword = passwordEncoder.encode(password);
        Optional<UserEntity> userDetails = userRepository.findByUsernameAndPassword(username, encodedPassword);
        if (userDetails.isEmpty()) {
            throw new UsernameNotFoundException(username);
        } else {
            return new User(username, password, new ArrayList<>());
        }
    }

    public UserDetails saveUser(String username, String password, String nickname, String email, String phoneNumber) throws Exception {
        userRepository.save(new UserEntity(username,password,nickname, email, phoneNumber));
        return new User(username, password, new ArrayList<>());
    }
}
