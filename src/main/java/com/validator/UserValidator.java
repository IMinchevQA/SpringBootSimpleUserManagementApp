package com.validator;

import com.model.Role;
import com.model.User;
import com.service.UserService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import static com.security.SecurityConstants.SECRET;
import static com.security.SecurityConstants.TOKEN_PREFIX;

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

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "userForm.username is empty!");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "userForm.password is empty!");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "userForm.passwordConfirm is empty!");

        if (!errors.hasErrors()) {

            if (user.getUsername().length() < 3 || user.getUsername().length() > 15) {
                errors.rejectValue("username", "Size.userForm.username must be btw. 5 and 15 symbols long!");
            }

            if (userData.findByUsername(user.getUsername()) != null) {
                errors.rejectValue("username", "Duplicate.userForm.username");
            }

            if (user.getPassword().length() < 5 || user.getPassword().length() > 15) {
                errors.rejectValue("password", "Size.userForm.password must be btw. 5 and 15 symbols long!");
            }

            if (!user.getPasswordConfirm().equals(user.getPassword())) {
                errors.rejectValue("passwordConfirm", "Password and PasswordConfirm mismatch!");
            }
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
