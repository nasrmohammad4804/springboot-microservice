package com.nasr.authorizationserver.service.impl;

import com.nasr.authorizationserver.domain.User;
import com.nasr.authorizationserver.dto.response.UserResponseDto;
import com.nasr.authorizationserver.repository.UserRepository;
import com.nasr.authorizationserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserWithRoleByUserName(String email) throws Exception {
        return userRepository.findWithRoleByEmail(email)
                .orElseThrow(() -> new IllegalStateException("dont find any user with email : " + email));

    }

    @Override
    public Boolean isExists() {
        return userRepository.isExists();
    }

    @Override
    @Transactional
    public List<UserResponseDto> saveAll(Iterable<User> users) {
        return userRepository.saveAll(users)
                .stream()
                .map(user -> UserResponseDto.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserByUserName(String email) {
        return userRepository.findByUserName(email);
    }
}
