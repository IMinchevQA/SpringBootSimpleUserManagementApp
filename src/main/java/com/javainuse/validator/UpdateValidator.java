package com.javainuse.validator;

import com.javainuse.model.UpdateTaskProgress;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by Ivan Minchev on 11/10/2017.
 */
@Component
public class UpdateValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return UpdateTaskProgress.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "message", "updateForm.message is empty!");
    }
}
