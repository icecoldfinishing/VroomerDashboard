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
        mv.addObject("title", "Vroomer - Location de voitures");
        return mv;
    }

    @GetMapping("/index")
    public ModelView home() {
        ModelView mv = new ModelView("index.html");
        mv.addObject("title", "Vroomer - Accueil");
        return mv;
    }

    @GetMapping("/about")
    public ModelView about() {
        ModelView mv = new ModelView("about.html");
        mv.addObject("title", "À propos de Vroomer");
        return mv;
    }

    @GetMapping("/services")
    public ModelView services() {
        ModelView mv = new ModelView("services.html");
        mv.addObject("title", "Nos services");
        return mv;
    }

    @GetMapping("/pricing")
    public ModelView pricing() {
        ModelView mv = new ModelView("pricing.html");
        mv.addObject("title", "Tarifs");
        return mv;
    }

    @GetMapping("/car")
    public ModelView cars() {
        ModelView mv = new ModelView("car.html");
        mv.addObject("title", "Nos voitures");
        return mv;
    }

    @GetMapping("/car-single")
    public ModelView carSingle() {
        ModelView mv = new ModelView("car-single.html");
        mv.addObject("title", "Détail voiture");
        return mv;
    }

    @GetMapping("/blog")
    public ModelView blog() {
        ModelView mv = new ModelView("blog.html");
        mv.addObject("title", "Blog");
        return mv;
    }

    @GetMapping("/blog-single")
    public ModelView blogSingle() {
        ModelView mv = new ModelView("blog-single.html");
        mv.addObject("title", "Article");
        return mv;
    }

    @GetMapping("/contact")
    public ModelView contact() {
        ModelView mv = new ModelView("contact.html");
        mv.addObject("title", "Contact");
        return mv;
    }
}
