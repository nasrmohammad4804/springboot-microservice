package com.nasr.authorizationserver.service;

import com.nasr.authorizationserver.domain.User;
import com.nasr.authorizationserver.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {

    User getUserWithRoleByUserName(String email) throws Exception;

    Boolean isExists();

    List<UserResponseDto> saveAll(Iterable<User> users);

    UserResponseDto getUserByUserName(String name);
}
