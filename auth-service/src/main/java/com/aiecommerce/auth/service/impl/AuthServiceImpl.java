package com.aiecommerce.auth.service.impl;

import com.aiecommerce.auth.dto.request.LoginRequest;
import com.aiecommerce.auth.dto.request.RefreshTokenRequest;
import com.aiecommerce.auth.dto.request.UserRegistrationDto;
import com.aiecommerce.auth.dto.response.TokenResponse;
import com.aiecommerce.auth.service.AuthService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final Keycloak keycloak;

    @Override
    public void register(UserRegistrationDto userRegistrationDto) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType("password");
        credentialRepresentation.setValue(userRegistrationDto.getPassword());
        credentialRepresentation.setTemporary(false);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setCredentials(List.of(credentialRepresentation));
        userRepresentation.setUsername(userRegistrationDto.getUsername());
        userRepresentation.setEmail(userRegistrationDto.getEmail());
        userRepresentation.setFirstName(userRegistrationDto.getFirstName());
        userRepresentation.setLastName(userRegistrationDto.getLastName());
        userRepresentation.setEnabled(true);

        Response response = keycloak.realm(realm).users().create(userRepresentation);

        if (response.getStatus() == HttpStatus.SC_CREATED) {
            return;
        } else if (response.getStatus() == HttpStatus.SC_CONFLICT) {
            throw new RuntimeException("User or Email already exists!");
        } else {
            throw new RuntimeException("Fail to create user with error: " + response.getStatus());
        }

    }

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        try {
            Keycloak userKeycloak1 = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(loginRequest.getUsername())
                    .password(loginRequest.getPassword())
                    .build();
            AccessTokenResponse accessTokenResponse = userKeycloak1.tokenManager().getAccessToken();
            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setAccessToken(accessTokenResponse.getToken());
            tokenResponse.setRefreshToken(accessTokenResponse.getRefreshToken());
            tokenResponse.setExpiresIn(String.valueOf(accessTokenResponse.getExpiresIn()));
            tokenResponse.setRefreshTokenExpireIn(String.valueOf(accessTokenResponse.getRefreshExpiresIn()));
            tokenResponse.setTokenType(accessTokenResponse.getTokenType());

            return tokenResponse;
        } catch (Exception e) {
            throw new RuntimeException("Invalid username and password ", e);
        }
    }


    @Override
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        try {
            Keycloak refreshKeycloak = KeycloakBuilder.builder().serverUrl(serverUrl).realm(realm).clientId(clientId).clientSecret(clientSecret).grantType("refresh_token")              // key difference from login
                    .authorization(request.getRefreshToken()) // pass the refresh token here
                    .build();

            AccessTokenResponse accessTokenResponse = refreshKeycloak.tokenManager().getAccessToken();

            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setAccessToken(accessTokenResponse.getToken());
            tokenResponse.setRefreshToken(accessTokenResponse.getRefreshToken());

            tokenResponse.setExpiresIn(String.valueOf(accessTokenResponse.getExpiresIn()));

            tokenResponse.setRefreshTokenExpireIn(String.valueOf(accessTokenResponse.getRefreshExpiresIn()));
            tokenResponse.setTokenType(accessTokenResponse.getTokenType());

            return tokenResponse;
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired refresh token", e);
        }
    }

}