package com.javainuse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ivan Minchev on 10/18/2017.
 */

@RestController
@EnableAutoConfiguration
@SpringBootApplication
public class SpringBootApp {
    public static void main(String[] args) {
//        Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        SpringApplication.run(SpringBootApp.class, args);



    }
}
