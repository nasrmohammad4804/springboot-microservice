package com.nasr.apigateway.dto.response;

import com.nasr.apigateway.external.response.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenInfoResponseDto {

    private UserInfoResponseDto userInfo;
    private String accessToken;
    private String refreshToken;
    private Instant accessTokenExpireAt;
    private List<String> authorities = new ArrayList<>();
}
