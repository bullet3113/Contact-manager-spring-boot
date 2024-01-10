package com.smartcontactmanagaer.SCMApplication.config;

import com.smartcontactmanagaer.SCMApplication.Entity.User;
import com.smartcontactmanagaer.SCMApplication.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // fetching user from database
        User user = userRepository.findByEmail(username);

        if(user == null) throw new UsernameNotFoundException("User not found");

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return customUserDetails;
    }
}
