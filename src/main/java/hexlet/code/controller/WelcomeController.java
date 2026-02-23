package hexlet.code.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    private String welcomeMessage;

    @GetMapping("/welcome")
    public String welcome() {
        String message = "Welcome to Spring";
        return message;
    }

}

