package com.vrommer.dashboard.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

@Controller
public class HelloController {

    @GetMapping("/")
    public String hello(HttpSession session, Model model) {
        return "views/home/home";
    }
}
