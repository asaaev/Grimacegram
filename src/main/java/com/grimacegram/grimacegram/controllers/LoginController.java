package com.grimacegram.grimacegram.controllers;

import com.grimacegram.grimacegram.error.ApiError;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.shared.CurrentUser;
import com.grimacegram.grimacegram.shared.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.Map;

@RestController
public class LoginController {
    /**
     * Endpoint for handling user login.
     * Upon successful login, returns the user ID of the logged-in user.
     *
     * @param loggedInUser the currently authenticated user, injected using @CurrentUser.
     * @return Map containing the ID of the logged-in user.
     */
    @PostMapping("/api/1.0/login")
    Map<String, Object> handleLogin(@CurrentUser User loggedInUser){
        // Old approach
        // User loggedInUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Returning the user ID of the logged-in user
        return Collections.singletonMap("id", loggedInUser.getUserId());
    }

}
