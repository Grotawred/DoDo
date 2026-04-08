package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/home")
    public String landingPage(){
        return "index";
    }
    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }
    @GetMapping("/register")
    public String showRegisterPage(){
        return "registration";
    }
    @PostMapping("/register")
    public String register(@RequestParam String login,
                           @RequestParam String password){
        if(userRepository.findByLogin(login).isPresent()){
            return "redirect:/register?error";
        }
        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        return "redirect:login";
    }

}
