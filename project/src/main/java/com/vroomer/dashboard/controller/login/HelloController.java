package com.vroomer.dashboard.controller.login;

import etu.sprint.model.ModelView;
import etu.sprint.annotation.AnnotationController;
import etu.sprint.annotation.GetMapping;

@AnnotationController("/home")
public class HelloController {

    @GetMapping("/")
    public ModelView hello() {
        ModelView mv = new ModelView();
        mv.setView("/templates/views/home/home.html");
        mv.addItem("message", "Bienvenue sur Vroomer!");
        return mv;
    }
}
