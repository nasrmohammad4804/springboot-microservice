package com.nasr.authorizationserver.config;

import com.nasr.authorizationserver.domain.User;
import com.nasr.authorizationserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        try {
            User user = userService.getUserWithRoleByUserName(email);
            return new CustomUserDetail(user);

        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

}
