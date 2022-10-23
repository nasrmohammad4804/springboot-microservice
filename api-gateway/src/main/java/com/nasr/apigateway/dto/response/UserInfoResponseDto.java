package com.nasr.apigateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDto {

    private Long ssoId;
    private String firstName;
    private String lastName;
    private String email;
}

