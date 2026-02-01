package etu.sprint.controller;

import etu.sprint.model.ModelView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur de pages utilisant ModelView du framework maison.
 * Les méthodes retournent un ModelView qui est converti en redirection
 * vers la vue HTML correspondante.
 */
@Controller
public class PageController {

    @GetMapping("/")
    public ModelView index() {
        ModelView mv = new ModelView("index.html");
        mv.addObject("title", "Dashboard - Accueil");
        return mv;
    }

    @GetMapping("/login")
    public ModelView login() {
        ModelView mv = new ModelView("login.html");
        mv.addObject("title", "Connexion");
        return mv;
    }

    @GetMapping("/register")
    public ModelView register() {
        ModelView mv = new ModelView("register.html");
        mv.addObject("title", "Inscription");
        return mv;
    }

    @GetMapping("/profile")
    public ModelView profile() {
        ModelView mv = new ModelView("profile.html");
        mv.addObject("title", "Profil");
        return mv;
    }

    @GetMapping("/cards")
    public ModelView cards() {
        ModelView mv = new ModelView("cards.html");
        mv.addObject("title", "Cartes");
        return mv;
    }

    @GetMapping("/charts")
    public ModelView charts() {
        ModelView mv = new ModelView("charts.html");
        mv.addObject("title", "Graphiques");
        return mv;
    }

    @GetMapping("/forms")
    public ModelView forms() {
        ModelView mv = new ModelView("form-elements.html");
        mv.addObject("title", "Formulaires");
        return mv;
    }

    @GetMapping("/icons")
    public ModelView icons() {
        ModelView mv = new ModelView("icons.html");
        mv.addObject("title", "Icônes");
        return mv;
    }

    @GetMapping("/gallery")
    public ModelView gallery() {
        ModelView mv = new ModelView("gallery.html");
        mv.addObject("title", "Galerie");
        return mv;
    }

    @GetMapping("/pricing")
    public ModelView pricing() {
        ModelView mv = new ModelView("pricing-cards.html");
        mv.addObject("title", "Tarifs");
        return mv;
    }
    
    @GetMapping("/users")
    public ModelView users() {
        ModelView mv = new ModelView("users-list.html");
        mv.addObject("title", "Liste des utilisateurs");
        return mv;
    }
}
