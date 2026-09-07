package com.aiecommerce.auth.service;


import com.aiecommerce.auth.dto.request.LoginRequest;
import com.aiecommerce.auth.dto.request.RefreshTokenRequest;
import com.aiecommerce.auth.dto.request.UserRegistrationDto;
import com.aiecommerce.auth.dto.response.TokenResponse;


public interface AuthService {
    public void register(UserRegistrationDto userRegistrationDto);

    public TokenResponse login(LoginRequest loginRequest);

    public TokenResponse refreshToken(RefreshTokenRequest request);
}
