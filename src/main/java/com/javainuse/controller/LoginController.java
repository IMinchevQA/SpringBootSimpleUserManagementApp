package com.javainuse.controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Ivan Minchev on 10/23/2017.
 */
@RestController
public class LoginController {

    @RequestMapping(value="/users/login", method = RequestMethod.POST)
    @ResponseBody
    public String sayHello(@RequestBody LoginObject payload) {
        String email = payload.getEmail();
        String password = payload.getPassword();
        return "Hello world!";
    }
}

class LoginObject {
    private String email;
    private String password;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
