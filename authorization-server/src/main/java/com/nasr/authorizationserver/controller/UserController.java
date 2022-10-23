package com.nasr.authorizationserver.controller;

import com.nasr.authorizationserver.dto.response.UserResponseDto;
import com.nasr.authorizationserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     *
     * @param authentication as authenticated user taken from SecurityContext
     * @return user info of logged in authorization server
     */
    @GetMapping
    public ResponseEntity<UserResponseDto> getUser(Authentication authentication){
        UserResponseDto user = userService.getUserByUserName(authentication.getName());
        return ResponseEntity.ok(user);
    }
}
