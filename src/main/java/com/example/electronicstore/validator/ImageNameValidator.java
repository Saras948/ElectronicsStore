package com.example.electronicstore.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageNameValidator implements ConstraintValidator<ImageNameValid,String> {

    private Logger logger = LoggerFactory.getLogger(ImageNameValidator.class);
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        logger.info("ImageNameValidator isValid() called : {}",value);
        //logic
        if(!value.isBlank())
        {
            if(value.endsWith(".jpg") || value.endsWith(".jpeg") || value.endsWith(".png"))
                return true;
        }


        return false;
    }
}
