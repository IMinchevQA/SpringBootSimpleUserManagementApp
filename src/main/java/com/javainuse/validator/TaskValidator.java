package com.javainuse.validator;

import com.javainuse.model.TaskEmployee;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by Ivan Minchev on 11/9/2017.
 */
@Component
public class TaskValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return TaskEmployee.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        TaskEmployee task = (TaskEmployee) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "taskForm.title is empty!");
        if (task.getTitle() != null
                && task.getTitle().length() < 5
                    || task.getTitle().length() > 30) {
            errors.rejectValue("title", "taskForm.title must be btw. 5 and 20 symbols long!");
        }
    }
}
