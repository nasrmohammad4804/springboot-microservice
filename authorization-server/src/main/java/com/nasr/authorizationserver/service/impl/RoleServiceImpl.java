package com.nasr.authorizationserver.service.impl;

import com.nasr.authorizationserver.domain.Role;
import com.nasr.authorizationserver.dto.response.RoleResponseDto;
import com.nasr.authorizationserver.repository.RoleRepository;
import com.nasr.authorizationserver.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository repository;

    @Override
    public RoleResponseDto getRoleById(Long id) {
        Role role = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("dont find any role with id : " + id));

        RoleResponseDto dto =new RoleResponseDto();
        BeanUtils.copyProperties(role,dto);
        return dto;
    }

    @Override
    public Role getRoleByName(String name) {

        return repository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("dont find any role with name : " + name));
    }

    @Override
    public Boolean isExists() {
        return repository.isExists();
    }

    @Override
    @Transactional
    public List<RoleResponseDto> saveAll(Iterable<Role> roles) {

        return repository.saveAll(roles)
                .stream()
                .map(role -> {
                    RoleResponseDto responseDto = new RoleResponseDto();
                    BeanUtils.copyProperties(role,responseDto);
                    return responseDto;
                })
                .collect(Collectors.toList());
    }
}
