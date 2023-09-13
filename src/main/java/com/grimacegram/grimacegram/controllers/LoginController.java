package com.grimacegram.grimacegram.controllers;

import com.grimacegram.grimacegram.shared.GenericResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @PostMapping("/api/1.0/login")
    void handleLogin(){
    }
}
