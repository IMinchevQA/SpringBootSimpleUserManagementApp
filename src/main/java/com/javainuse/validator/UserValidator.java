package com.javainuse.validator;

import com.javainuse.model.Employee;
import com.javainuse.model.Employer;
import com.javainuse.model.Role;
import com.javainuse.model.User;
import com.javainuse.service.EmployeeService;
import com.javainuse.service.UserService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import static com.javainuse.security.SecurityConstants.SECRET;
import static com.javainuse.security.SecurityConstants.TOKEN_PREFIX;

/**
 * Created by Ivan Minchev on 10/24/2017.
 */
@Component
public class UserValidator implements Validator {

    @Autowired
    private UserService userData;


    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.getUsername().length() < 3 || user.getUsername().length() > 15) {
            errors.rejectValue("username", "Size.userForm.username");
        }
        if (userData.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "Duplicate.userForm.username");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.getPassword().length() < 5 || user.getPassword().length() > 15) {
            errors.rejectValue("password", "Size.userForm.password");
        }
        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirm", "Password and PasswordConfirm mismatch!");
        }

    }

    public boolean isUserInRole(String token, String requiredRole) {
        String requestUsername = Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();

        User requestUser = this.userData.findByUsername(requestUsername);

        if (requestUser == null) {
            throw new UsernameNotFoundException("Request User is not registered in the system!");
        }

        Role roleRequestUser = (Role) requestUser.getRoles().stream().toArray()[0];
        return roleRequestUser.getName().equals(requiredRole);
    }
}
