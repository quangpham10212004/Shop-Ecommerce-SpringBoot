package com.aiecommerce.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRegistrationDto {
    Boolean enabled;
    String username;
    String password;
    String email;
    String firstName;
    String lastName;
}
