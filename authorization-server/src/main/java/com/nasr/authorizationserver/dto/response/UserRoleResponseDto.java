package com.nasr.authorizationserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleResponseDto {
    private Long id;
    private String userName;
    private String password;
    private RoleResponseDto role;
}
