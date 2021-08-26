package com.example.account.controller;

import java.util.ArrayList;
import java.util.Optional;

import com.example.account.config.JwtTokenUtil;
import com.example.account.model.JwtRequest;
import com.example.account.model.JwtResponse;
import com.example.account.model.Response;
import com.example.account.model.UserEntity;
import com.example.account.repository.UserRepository;
import com.example.account.service.JwtUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class JwtAuthenticationController{
        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private JwtTokenUtil jwtTokenUtil;
        
        @Autowired
        private JwtUserDetailsService userDetailsService;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
        public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception{
            // authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            // authenticate(userDetails);
            // UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
                        
            // final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            // final String token = jwtTokenUtil.generateToken(userDetails);

            // return ResponseEntity.ok(new JwtResponse(token));

            Optional<UserEntity> userEntity = userRepository.findByUsername(authenticationRequest.getUsername());
            if (userEntity.isEmpty()){
                return ResponseEntity.badRequest().body(new Response("wrong username"));
            } 
            if(passwordEncoder.matches(authenticationRequest.getPassword(), userEntity.get().getPassword())) {
                final UserDetails userDetails = new User(authenticationRequest.getUsername(), userEntity.get().getPassword(), new ArrayList<>());
                final String token = jwtTokenUtil.generateToken(userDetails);
                return ResponseEntity.ok(new JwtResponse(token));
            } else {
                return ResponseEntity.badRequest().body(new Response("wrong password"));
            }
        }
        
        @RequestMapping(value = "/signup", method = RequestMethod.POST)
        public ResponseEntity<?> generateAtuenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
            if(userRepository.existsByUsername(authenticationRequest.getUsername())) {
                return ResponseEntity.badRequest().body(new Response("user is exists"));
            }
            final String encodedPassword = passwordEncoder.encode(authenticationRequest.getPassword());
            final UserDetails userDetails = userDetailsService.saveUser(
                authenticationRequest.getUsername(), 
                encodedPassword, 
                authenticationRequest.getNickname());
            final String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(token));
        }
        
        @RequestMapping(value = "/exists", method = RequestMethod.GET)
        public ResponseEntity<?> userIdIsExists(long userId) {
            if (userRepository.existsById(userId)) {
                return ResponseEntity.ok(new Response("True"));
            } else {
                return ResponseEntity.ok(new Response("False"));
            }
        }

        @RequestMapping(value = "/hello", method = RequestMethod.GET)
        public String hello() {
            return "Hello World";
        }

        private void authenticate(String username, String password) throws Exception {
            try {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
                authenticationManager.authenticate(token);
                // authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
            } catch (DisabledException e) {
                throw new Exception("USER_DISABLED", e);
            } catch (BadCredentialsException e) {
                throw new Exception("INVALID_CREDENTIALS", e);
            }
        }
}
