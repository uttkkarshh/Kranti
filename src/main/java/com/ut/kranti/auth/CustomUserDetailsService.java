package com.ut.kranti.auth;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ut.kranti.user.UserProfile;
import com.ut.kranti.user.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userrepo;

  

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserProfile userEntity = userrepo.findByUsername(username).get();
        if (userEntity == null) {
        	System.out.println("hel");
            throw new UsernameNotFoundException("User not found");
        }

        return new UserPrincipal(userEntity);
    }
}