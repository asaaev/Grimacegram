package com.grimacegram.grimacegram.error;

import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    @Autowired
    UserRepository userRepository;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        User inDB = userRepository.findByUserName(s);
        if(inDB == null){
            return true;
        }
        return false;
    }
}
