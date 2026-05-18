package com.dipanshushukla.cop_map_auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dipanshushukla.cop_map_auth_service.repository.UserRepository;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String badgeNumber) throws UsernameNotFoundException {
        return repository.findByBadgeNumber(badgeNumber)
                .orElseThrow(() -> new UsernameNotFoundException("No officer found with Badge ID: " + badgeNumber));
    }

}
