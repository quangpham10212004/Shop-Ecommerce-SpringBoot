package com.aiecommerce.auth.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    String accessToken;
    String refreshToken;
    String tokenType;
    String expiresIn;
    String refreshTokenExpireIn;
}
