package com.grimacegram.grimacegram.error;

import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.UserRepository;
import com.grimacegram.grimacegram.shared.UniqueUsername;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * Validator for ensuring that a username is unique across the application.
 * Implements the ConstraintValidator interface, allowing it to be used as a custom validation annotation.
 * This is used to validate that the username provided is not already taken by another user.
 */
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    @Autowired
    UserRepository userRepository;
    /**
     * Checks if the provided username value is unique.
     *
     * @param username the username to be checked.
     * @param constraintValidatorContext context in which the constraint is evaluated.
     * @return true if the username is unique, false otherwise.
     *
     * This method queries the UserRepository to see if the username is already present.
     * If the username is found, it returns false indicating a validation failure.
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        User inDB = userRepository.findByUsername(s);
        if(inDB == null){
            return true;
        }
        return false;
    }
}
