package com.javainuse.validator;

import com.javainuse.model.Employee;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import static com.javainuse.validator.ValidatorConstants.NUMBERS_PATTERN;

/**
 * Created by Ivan Minchev on 11/6/2017.
 */
@Component
public class EmployeeByEmployerUpdateValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Employee.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Employee employee = (Employee) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "employeeNumber", "employeeForm.employeeNumber is empty!");
        if (null != employee.getEmployeeNumber()) {

            if (!employee.getEmployeeNumber().matches(NUMBERS_PATTERN)) {
                errors.rejectValue("employeeNumber", "employeeForm.employeeNumber must have only numbers!");
            }


            if(employee.getEmployeeNumber().length() < 5 || employee.getEmployeeNumber().length() > 10) {
                errors.rejectValue("employeeNumber", "employeeForm.employeeNumber must be btw. 5 and 10 symbols long!");
            }

        }


        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "departmentID", "employeeForm.departmentID is empty!");
        if (null != employee.getDepartmentID()) {

            if (!employee.getDepartmentID().matches(NUMBERS_PATTERN)) {
                errors.rejectValue("departmentID", "departmentID.employeeNumber must have only numbers!");
            }

            if (employee.getDepartmentID().length() < 3 || employee.getDepartmentID().length() > 10) {
                errors.rejectValue("departmentID", "employeeForm.departmentID must be btw. 3 and 10 symbols long!");
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "job", "employeeForm.job is empty!");
        if (null != employee.getJob()
                && (employee.getJob().length() < 5
                || employee.getDepartmentID().length() > 30)) {
            errors.rejectValue("job", "employeeForm.job must be btw. 5 and 30 symbols long!");
        }


        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "formalEducationYears", "employeeForm.formalEducationYears is empty!");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "yearSalary", "employeeForm.yearSalary is empty!");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "yearBonus", "employeeForm.yearBonus is empty!");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "commission", "employeeForm.commission is empty!");

    }
}
