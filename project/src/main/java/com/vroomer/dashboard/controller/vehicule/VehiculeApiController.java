package com.vroomer.dashboard.controller.vehicule;

import java.util.List;
import com.vroomer.dashboard.dto.VehiculeDTO;
import com.vroomer.dashboard.model.vehicule.Vehicule;
import com.vroomer.dashboard.model.carburant.Carburant;
import com.vroomer.dashboard.service.vehicule.VehiculeService;
import com.vroomer.dashboard.service.carburant.CarburantService;

import etu.sprint.annotation.AnnotationController;
import etu.sprint.annotation.GetMapping;
import etu.sprint.annotation.PostMapping;
import etu.sprint.annotation.RequestParameter;
import etu.sprint.annotation.RestAPI;

@org.springframework.stereotype.Component
@AnnotationController("/api")

public class VehiculeApiController {
    @org.springframework.beans.factory.annotation.Autowired
    private VehiculeService vehiculeService;

    @org.springframework.beans.factory.annotation.Autowired
    private CarburantService carburantService;

    @RestAPI
    @GetMapping("/vehicules")
    public List<VehiculeDTO> getAllVehicules() {
        List<Vehicule> vehicules = vehiculeService.getAll();
        return vehicules.stream().map(VehiculeDTO::new).toList();
    }

    @RestAPI
    @GetMapping("/vehicules/byid")
    public VehiculeDTO getVehiculeById(@RequestParameter("id") Long id) {
        Vehicule vehicule = vehiculeService.findById(id);
        return vehicule != null ? new VehiculeDTO(vehicule) : null;
    }

    @RestAPI
    @PostMapping("/vehicules/create")
    public VehiculeDTO createVehicule(
        @RequestParameter("ref") String ref,
        @RequestParameter("nbPlace") Integer nbPlace,
        @RequestParameter("carburantId") Long carburantId
    ) {
        Carburant carburant = carburantService.findById(carburantId);
        if (carburant == null) {
            throw new RuntimeException("Carburant introuvable avec l'ID: " + carburantId);
        }
        
        Vehicule vehicule = new Vehicule();
        vehicule.setRef(ref);
        vehicule.setNbPlace(nbPlace);
        vehicule.setCarburant(carburant);
        
        Vehicule savedVehicule = vehiculeService.save(vehicule);
        return new VehiculeDTO(savedVehicule);
    }

    @RestAPI
    @PostMapping("/vehicules/update")
    public VehiculeDTO updateVehicule(
        @RequestParameter("id") Long id,
        @RequestParameter("ref") String ref,
        @RequestParameter("nbPlace") Integer nbPlace,
        @RequestParameter("carburantId") Long carburantId
    ) {
        Vehicule vehicule = vehiculeService.findById(id);
        if (vehicule == null) {
            throw new RuntimeException("Véhicule introuvable avec l'ID: " + id);
        }

        Carburant carburant = carburantService.findById(carburantId);
        if (carburant == null) {
            throw new RuntimeException("Carburant introuvable avec l'ID: " + carburantId);
        }

        vehicule.setRef(ref);
        vehicule.setNbPlace(nbPlace);
        vehicule.setCarburant(carburant);
        
        Vehicule updatedVehicule = vehiculeService.save(vehicule);
        return new VehiculeDTO(updatedVehicule);
    }

    @RestAPI
    @PostMapping("/vehicules/delete")
    public String deleteVehicule(@RequestParameter("id") Long id) {
        vehiculeService.delete(id);
        return "Véhicule supprimé avec succès";
    }
}