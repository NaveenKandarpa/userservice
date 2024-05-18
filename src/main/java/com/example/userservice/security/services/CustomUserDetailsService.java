package com.example.userservice.security.services;

import com.example.userservice.models.User;
import com.example.userservice.repositories.UserRepository;
import com.example.userservice.security.models.CustomUserDetails;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if(optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User with email: " + username + " doesn't exist");
        }
        User user = optionalUser.get();
        return new CustomUserDetails(user);
//        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
//                .username("user")
//                .password("$2a$12$gBj/r/lJ8goebxJ6Im6D3e96mZMWiHdBlb1Loz3B2QSqBSy4fQ13K")
//                .roles("ADMIN").authorities("ADMIN")
//                .build();

//        return userDetails;
    }

}
