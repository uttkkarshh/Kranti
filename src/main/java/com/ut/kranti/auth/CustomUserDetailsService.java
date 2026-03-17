package com.ut.kranti.auth;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ut.kranti.user.UserProfile;
import com.ut.kranti.user.UserRepository;

import java.util.Optional;

// logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userrepo;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("loadUserByUsername called with username={}", username);
        Optional<UserProfile> opt = userrepo.findByUsername(username);
        if (!opt.isPresent()) {
            logger.warn("User not found for username={}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        UserProfile userEntity = opt.get();
        logger.debug("User found: id={}, username={}", userEntity.getId(), userEntity.getUsername());
        return new UserPrincipal(userEntity);
    }
}