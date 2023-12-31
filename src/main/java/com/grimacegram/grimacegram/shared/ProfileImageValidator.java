package com.grimacegram.grimacegram.shared;

import com.grimacegram.grimacegram.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Base64;

public class ProfileImageValidator implements ConstraintValidator<ProfileImage, String> {

    @Autowired
    FileService fileService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null) {
            return true;
        }
        byte[] decodedBytes = Base64.getDecoder().decode(value);
        String fileType = fileService.detectType(decodedBytes);
        if(fileType.equalsIgnoreCase("image/jpeg") || fileType.equalsIgnoreCase("image/png")){
            return true;
        }
        return false;
    }
}
