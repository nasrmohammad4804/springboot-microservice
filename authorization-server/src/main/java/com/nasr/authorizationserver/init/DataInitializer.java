package com.nasr.authorizationserver.init;

import com.nasr.authorizationserver.domain.Role;
import com.nasr.authorizationserver.domain.User;
import com.nasr.authorizationserver.service.RoleService;
import com.nasr.authorizationserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        if (!roleService.isExists())
            createDefaultRole();

        if (!userService.isExists())
            createDefaultUser();

    }

    private void createDefaultUser() {

        List<User> users = new ArrayList<>();

        RoleConsumer consumer = (customRole, user) -> {
            user.setRole(customRole);
            users.add(user);
        };

        consumer.consume(
                roleService.getRoleByName("ADMIN"),
                        new User("mohammad", "nasr", "nasrmohammad4804@gmail.com", passwordEncoder.encode("1234")
                        )
        );
        consumer.consume(
                roleService.getRoleByName("USER"),
                new User("javad", "zare", "javad@gmail.com", passwordEncoder.encode("4567")
                )
        );

        consumer.consume(
                roleService.getRoleByName("SUPER_ADMIN"),
                new User("aida", "fallah", "aida6529@gmail.com", passwordEncoder.encode("5629")
                )
        );

        userService.saveAll(users);
    }

    private void createDefaultRole() {
        List<Role> roles = List.of(
                new Role("ADMIN"),
                new Role("USER"),
                new Role("SUPER_ADMIN")
        );
        roleService.saveAll(roles);
    }
}

@FunctionalInterface
interface RoleConsumer {
    void consume(Role role, User user);
}