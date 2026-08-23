package com.aiecommerce.auth.controller;

import com.aiecommerce.auth.dto.request.UserRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    @PostMapping
    public UserRegistrationDto register(){

    }
}
