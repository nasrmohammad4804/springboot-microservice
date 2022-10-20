package com.nasr.authorizationserver.service;

import com.nasr.authorizationserver.domain.Role;
import com.nasr.authorizationserver.dto.response.RoleResponseDto;

import java.util.List;

public interface RoleService {

    RoleResponseDto getRoleById(Long id);

    Role getRoleByName(String name);

    Boolean isExists();

    List<RoleResponseDto> saveAll(Iterable<Role> roles);

}
