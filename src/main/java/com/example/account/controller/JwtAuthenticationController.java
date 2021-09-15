package com.example.account.controller;

import java.util.ArrayList;
import java.util.Optional;

import com.example.account.config.JwtTokenUtil;
import com.example.account.model.JwtRequest;
import com.example.account.model.JwtResponse;
import com.example.account.model.Response;
import com.example.account.model.UserEntity;
import com.example.account.model.UserResponse;
import com.example.account.repository.UserRepository;
import com.example.account.service.JwtUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
                return ResponseEntity.badRequest().body(new Response("wrong username", null));
            } 
            if(passwordEncoder.matches(authenticationRequest.getPassword(), userEntity.get().getPassword())) {
                // final UserDetails userDetails = new User(authenticationRequest.getUsername(), userEntity.get().getPassword(), new ArrayList<>());
                final UserDetails userDetails = new User(userEntity.get().getUsername(), userEntity.get().getPassword(), new ArrayList<>());
                final String token = jwtTokenUtil.generateToken(userDetails);
                UserResponse userResponse = new UserResponse(
                    userEntity.get().getId(),
                    userEntity.get().getUsername(),
                    userEntity.get().getNickname(),
                    userEntity.get().getEmail(),
                    userEntity.get().getPhoneNumber()
                );
                return ResponseEntity.ok(new JwtResponse(token, userResponse));
            } else {
                return ResponseEntity.badRequest().body(new Response("wrong password", null));
            }
        }
        
        @RequestMapping(value = "/signup", method = RequestMethod.POST)
        public ResponseEntity<?> generateAtuenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
            if(userRepository.existsByUsername(authenticationRequest.getUsername())) {
                return ResponseEntity.badRequest().body(new Response("user is exists", null));
            }
            final String encodedPassword = passwordEncoder.encode(authenticationRequest.getPassword());
            final UserDetails userDetails = userDetailsService.saveUser(
                authenticationRequest.getUsername(), 
                encodedPassword, 
                authenticationRequest.getNickname(),
                authenticationRequest.getEmail(),
                authenticationRequest.getPhoneNumber());
            final String token = jwtTokenUtil.generateToken(userDetails);

            Optional<UserEntity> userEntity = userRepository.findByUsername(authenticationRequest.getUsername());
            UserEntity user = userEntity.get();
            UserResponse userResponse = new UserResponse(
                user.getId(), 
                user.getUsername(), 
                user.getNickname(),
                user.getEmail(),
                user.getPhoneNumber());
            return ResponseEntity.ok(new JwtResponse(token, userResponse));
        }
        
        @RequestMapping(value = "/exists", method = RequestMethod.GET)
        public ResponseEntity<?> userIdIsExists(long userId) {
            if (userRepository.existsById(userId)) {
                return ResponseEntity.ok(new Response("True", null));
            } else {
                return ResponseEntity.ok(new Response("False", null));
            }
        }

        @RequestMapping(value = "/id", method = RequestMethod.GET)
        public ResponseEntity<?> userIdFromUsername(String username) {
            Optional<UserEntity> userEntity = userRepository.findByUsername(username);
            if (!userEntity.isPresent()){
                return ResponseEntity.badRequest().body(new Response("wrong username", null));
            }
            return ResponseEntity.ok().body(new Response(String.valueOf(userEntity.get().getId()), null));
        }

        @RequestMapping(value = "/hello", method = RequestMethod.POST)
        public String hello() {
            return "Hello World";
        }

        @RequestMapping(value = "/info/{username}", method = RequestMethod.GET)
        public ResponseEntity<?> userInfoFromUsername(@PathVariable String username){
            Optional<UserEntity> userEntity = userRepository.findByUsername(username);
            if (!userEntity.isPresent()){
                return ResponseEntity.badRequest().body(new Response("wrong username", null));
            }
            UserResponse userResponse = new UserResponse(
                    userEntity.get().getId(),
                    userEntity.get().getUsername(),
                    userEntity.get().getNickname(),
                    userEntity.get().getEmail(),
                    userEntity.get().getPhoneNumber()
                );
            return ResponseEntity.ok().body(new Response("get user information",userResponse));
        }

        @RequestMapping(value = "/info/id/{userId}", method = RequestMethod.GET)
        public ResponseEntity<?> userInfoFromId(@PathVariable long userId){
            Optional<UserEntity> userEntity = userRepository.findById(userId);
            if (!userEntity.isPresent()){
                return ResponseEntity.ok().body(new Response("wrong username", null));
            }
            UserResponse userResponse = new UserResponse(
                    userEntity.get().getId(),
                    userEntity.get().getUsername(),
                    userEntity.get().getNickname(),
                    userEntity.get().getEmail(),
                    userEntity.get().getPhoneNumber()
                );
            return ResponseEntity.ok().body(new Response("get user information",userResponse));
        }
        
        @RequestMapping(value = "/info", method = RequestMethod.GET)
        public ResponseEntity<?> userInfoFromJwt(@RequestHeader HttpHeaders headers){
            String auth = headers.getFirst("Authorization");
            String token = auth.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(token);
            
            Optional<UserEntity> userEntity = userRepository.findByUsername(username);
            if (!userEntity.isPresent()){
                return ResponseEntity.badRequest().body(new Response("wrong user", null));
            }
            UserResponse userResponse = new UserResponse(
                    userEntity.get().getId(),
                    userEntity.get().getUsername(),
                    userEntity.get().getNickname(),
                    userEntity.get().getEmail(),
                    userEntity.get().getPhoneNumber()
                );
            return ResponseEntity.ok().body(new Response("get user information",userResponse));
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
