package com.vroomer.dashboard.controller.carburant;

import java.util.List;
import com.vroomer.dashboard.model.carburant.Carburant;
import com.vroomer.dashboard.service.carburant.CarburantService;

import etu.sprint.model.ModelView;
import etu.sprint.annotation.AnnotationController;
import etu.sprint.annotation.GetMapping;
import etu.sprint.annotation.PostMapping;
import etu.sprint.annotation.RequestParameter;

@org.springframework.stereotype.Component
@AnnotationController("/carburants")
public class CarburantPageController {
    @org.springframework.beans.factory.annotation.Autowired
    private CarburantService carburantService;

    @GetMapping("")
    public ModelView listCarburants() {
        List<Carburant> carburants = carburantService.getAll();
        ModelView mv = new ModelView();
        mv.setView("views/carburant/list.html");
        mv.addItem("carburants", carburants);
        return mv;
    }

    @GetMapping("/insert")
    public ModelView insertCarburantForm() {
        ModelView mv = new ModelView();
        mv.setView("views/carburant/insert.html");
        return mv;
    }

    @GetMapping("/detail")
    public ModelView detailCarburant(@RequestParameter("id") Long id) {
        ModelView mv = new ModelView();
        Carburant carburant = carburantService.findById(id);
        if (carburant == null) {
            mv.setView("views/carburant/list.html");
            mv.addItem("carburants", carburantService.getAll());
            mv.addItem("message", "Carburant introuvable.");
            mv.addItem("messageType", "danger");
            return mv;
        }
        mv.setView("views/carburant/detail.html");
        mv.addItem("carburant", carburant);
        return mv;
    }

    @PostMapping("/insert")
    public ModelView insertCarburant(@RequestParameter("type") String type) {
        ModelView mv = new ModelView();
        try {
            Carburant carburant = new Carburant();
            carburant.setType(type);
            carburantService.save(carburant);
            mv.setView("views/carburant/list.html");
            mv.addItem("carburants", carburantService.getAll());
            mv.addItem("message", "Carburant ajouté avec succès !");
            mv.addItem("messageType", "success");
        } catch (Exception e) {
            mv.setView("views/carburant/insert.html");
            mv.addItem("message", "Erreur lors de l'ajout: " + e.getMessage());
            mv.addItem("messageType", "danger");
        }
        return mv;
    }

    @GetMapping("/edit")
    public ModelView editCarburantForm(@RequestParameter("id") Long id) {
        ModelView mv = new ModelView();
        Carburant carburant = carburantService.findById(id);
        if (carburant == null) {
            mv.setView("views/carburant/list.html");
            mv.addItem("carburants", carburantService.getAll());
            mv.addItem("message", "Carburant introuvable.");
            mv.addItem("messageType", "danger");
            return mv;
        }
        mv.setView("views/carburant/edit.html");
        mv.addItem("carburant", carburant);
        return mv;
    }

    @PostMapping("/update")
    public ModelView updateCarburant(
        @RequestParameter("id") Long id,
        @RequestParameter("type") String type
    ) {
        ModelView mv = new ModelView();
        try {
            Carburant carburant = carburantService.findById(id);
            if (carburant == null) {
                mv.setView("views/carburant/list.html");
                mv.addItem("carburants", carburantService.getAll());
                mv.addItem("message", "Carburant introuvable.");
                mv.addItem("messageType", "danger");
                return mv;
            }
            carburant.setType(type);
            carburantService.save(carburant);
            
            mv.setView("views/carburant/list.html");
            mv.addItem("carburants", carburantService.getAll());
            mv.addItem("message", "Carburant mis à jour avec succès !");
            mv.addItem("messageType", "success");
        } catch (Exception e) {
            Carburant carburant = carburantService.findById(id);
            mv.setView("views/carburant/edit.html");
            mv.addItem("carburant", carburant);
            mv.addItem("message", "Erreur lors de la mise à jour: " + e.getMessage());
            mv.addItem("messageType", "danger");
        }
        return mv;
    }

    @PostMapping("/delete")
    public ModelView deleteCarburant(@RequestParameter("id") Long id) {
        ModelView mv = new ModelView();
        try {
            carburantService.delete(id);
            mv.setView("views/carburant/list.html");
            mv.addItem("carburants", carburantService.getAll());
            mv.addItem("message", "Carburant supprimé avec succès !");
            mv.addItem("messageType", "success");
        } catch (Exception e) {
            mv.setView("views/carburant/list.html");
            mv.addItem("carburants", carburantService.getAll());
            mv.addItem("message", "Erreur lors de la suppression: " + e.getMessage());
            mv.addItem("messageType", "danger");
        }
        return mv;
    }
}