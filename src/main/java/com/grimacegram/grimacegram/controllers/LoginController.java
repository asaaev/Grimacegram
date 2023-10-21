package com.grimacegram.grimacegram.controllers;

import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.shared.CurrentUser;
import com.grimacegram.grimacegram.vm.UserVM;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    /**
     * Endpoint for handling user login.
     * The method returns the details of the logged-in user with a view filter applied.
     *
     * @JsonView(Views.Base.class): Specifies that only the fields annotated with the Base view
     * in the User model should be serialized in the response.
     * Sensitive information, like the password, will be omitted from the JSON output.
     *
     * @param loggedInUser The currently authenticated user, injected using @CurrentUser.
     * @return The logged-in User object, which will be converted to JSON using the specified view.
     */
    @PostMapping("/api/1.0/login")
    UserVM handleLogin(@CurrentUser User loggedInUser){

        return new UserVM(loggedInUser);
    }

}
