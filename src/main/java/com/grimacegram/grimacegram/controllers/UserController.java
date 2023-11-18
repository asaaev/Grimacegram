package com.grimacegram.grimacegram.controllers;

import com.grimacegram.grimacegram.error.ApiError;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.services.UserService;
import com.grimacegram.grimacegram.shared.CurrentUser;
import com.grimacegram.grimacegram.shared.GenericResponse;
import com.grimacegram.grimacegram.vm.UserUpdateVM;
import com.grimacegram.grimacegram.vm.UserVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0")
public class UserController {
    @Autowired
    UserService userService;


    @PostMapping("/users")
    GenericResponse createUser(@Valid @RequestBody User user){
        userService.save(user);
        return new GenericResponse("User");
    }
    /**
     * Endpoint to fetch a list of users with pagination support.
     * Excludes the currently logged-in user from the list.
     *
     * @param loggedInUser The User object of the currently logged-in user, injected by @CurrentUser.
     * @param page The Pageable object containing the pagination information.
     * @return A Page object wrapped around a list of UserVM (View Model) objects.
     */
    @GetMapping("/users")
    Page<UserVM> getUsers(@CurrentUser User loggedInUser, Pageable page){
        return userService.getUsers(loggedInUser, page).map(UserVM::new);
    }
    @GetMapping("/users/{username}")
    UserVM getUserByName(@PathVariable String username){
        User user = userService.getByUsername(username);
        return new UserVM(user);
    }
    @PutMapping("/users/{id:[0-9]+}")
    @PreAuthorize("#id == principal.userId")
    UserVM updateUser(@PathVariable long id,@Valid @RequestBody(required = false) UserUpdateVM userUpdate){
        User updated = userService.update(id, userUpdate);
        return new UserVM(updated);

    }


}
