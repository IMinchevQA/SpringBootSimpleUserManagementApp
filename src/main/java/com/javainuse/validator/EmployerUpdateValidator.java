package com.javainuse.validator;

import com.javainuse.model.Employer;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import static com.javainuse.validator.ValidatorConstants.*;

/**
 * Created by Ivan Minchev on 11/7/2017.
 */
@Component
public class EmployerUpdateValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Employer.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Employer employer = (Employer) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "employerForm.firstName is empty!");
        if (null != employer.getFirstName()) {
            if (!employer.getFirstName().matches(LETTERS_PATTERN)) {
                errors.rejectValue("firstName", "employerForm.firstName must must have only letters!");
            }

            if(employer.getFirstName().length() < 3 || employer.getFirstName().length() > 15) {
                errors.rejectValue("firstName", "employerForm.firstName must be btw. 3 and 15 symbols long!");
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "middleInitial", "employerForm.middleInitial is empty!");
        if (null != employer.getMiddleInitial()) {
            if (!employer.getMiddleInitial().matches(UPPER_CASE_LETTER)) {
                errors.rejectValue("middleInitial", "employerForm.middleInitial must be a single upper case letter!");
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "employerForm.lastName is empty!");
        if (null != employer.getLastName()) {
            if (!employer.getLastName().matches(LETTERS_PATTERN)) {
                errors.rejectValue("lastName", "employerForm.lastName must have only letters!");
            }

            if (employer.getLastName().length() < 3 || employer.getLastName().length() > 15) {
                errors.rejectValue("lastName", "employerForm.lastName must be btw. 3 and 15 symbols long!");
            }
        }
    }
}
