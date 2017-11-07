package com.javainuse.validator;

import com.javainuse.model.Employee;
import com.javainuse.model.Role;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import static com.javainuse.validator.ValidatorConstants.LETTERS_PATTERN;

/**
 * Created by Ivan Minchev on 11/6/2017.
 */
@Component
public class EmployeeByEmployeeUpdateValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Employee.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Employee employee = (Employee) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "employeeForm.firstName is empty!");
        if (null != employee.getFirstName()) {
            if (!employee.getFirstName().matches(LETTERS_PATTERN)) {
                errors.rejectValue("firstName", "employeeForm.firstName must must have only letters!");
            }

            if (employee.getFirstName().length() < 3 || employee.getFirstName().length() > 15) {
                errors.rejectValue("firstName", "employeeForm.firstName must be btw. 3 and 15 symbols long!");
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "middleInitial", "employeeForm.middleInitial is empty!");
        if (null != employee.getMiddleInitial()) {
            if (!employee.getMiddleInitial().matches(LETTERS_PATTERN)) {
                errors.rejectValue("middleInitial", "employeeForm.middleInitial must be a letter!");
            }

            if (employee.getMiddleInitial().length() != 1) {
                errors.rejectValue("middleInitial", "employeeForm.middleInitial must be only 1 symbol long!");
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "employeeForm.lastName is empty!");
        if (null != employee.getLastName()
                && (employee.getLastName().length() < 3
                    || employee.getLastName().length() > 15)) {
            errors.rejectValue("lastName", "employeeForm.lastName must be btw. 3 and 15 symbols long!");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "employeeForm.phoneNumber is empty!");
        if (null != employee.getPhoneNumber()) {

            if (!employee.getPhoneNumber().matches("[0-9]+")) {
                errors.rejectValue("lastName", "employeeForm.lastName must have only numbers!");
            }

            if (employee.getPhoneNumber().length() < 10
                    || employee.getPhoneNumber().length() > 20) {
                errors.rejectValue("phoneNumber", "employeeForm.phoneNumber must be btw. 10 and 20 symbols long!");
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sex", "employeeForm.sex is empty!");
        if (null != employee.getSex() && employee.getMiddleInitial().length() != 1) {
            errors.rejectValue("sex", "employeeForm.sex must be must be only 1 symbol long!");
        }
    }
}
