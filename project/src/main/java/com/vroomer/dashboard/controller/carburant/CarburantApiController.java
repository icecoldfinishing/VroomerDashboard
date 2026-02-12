package com.vroomer.dashboard.controller.carburant;

import java.util.List;
import com.vroomer.dashboard.model.carburant.Carburant;
import com.vroomer.dashboard.service.carburant.CarburantService;

import etu.sprint.annotation.AnnotationController;
import etu.sprint.annotation.GetMapping;
import etu.sprint.annotation.PostMapping;
import etu.sprint.annotation.RequestParameter;
import etu.sprint.annotation.RestAPI;

@org.springframework.stereotype.Component
@AnnotationController("/api")
public class CarburantApiController {
    @org.springframework.beans.factory.annotation.Autowired
    private CarburantService carburantService;

    @RestAPI
    @GetMapping("/carburants")
    public List<Carburant> getAllCarburants() {
        return carburantService.getAll();
    }

    @RestAPI
    @GetMapping("/carburants/byid")
    public Carburant getCarburantById(@RequestParameter("id") Long id) {
        return carburantService.findById(id);
    }

    @RestAPI
    @PostMapping("/carburants/create")
    public Carburant createCarburant(@RequestParameter("type") String type) {
        Carburant carburant = new Carburant();
        carburant.setType(type);
        return carburantService.save(carburant);
    }

    @RestAPI
    @PostMapping("/carburants/update")
    public Carburant updateCarburant(
        @RequestParameter("id") Long id,
        @RequestParameter("type") String type
    ) {
        Carburant carburant = carburantService.findById(id);
        if (carburant == null) {
            throw new RuntimeException("Carburant introuvable avec l'ID: " + id);
        }
        carburant.setType(type);
        return carburantService.save(carburant);
    }

    @RestAPI
    @PostMapping("/carburants/delete")
    public String deleteCarburant(@RequestParameter("id") Long id) {
        carburantService.delete(id);
        return "Carburant supprimé avec succès";
    }
}