package com.vroomer.dashboard.controller.vehicule;

import java.util.List;
import com.vroomer.dashboard.dto.VehiculeDTO;
import com.vroomer.dashboard.model.vehicule.Vehicule;
import com.vroomer.dashboard.model.carburant.Carburant;
import com.vroomer.dashboard.service.vehicule.VehiculeService;
import com.vroomer.dashboard.service.carburant.CarburantService;

import etu.sprint.model.ModelView;
import etu.sprint.annotation.AnnotationController;
import etu.sprint.annotation.GetMapping;
import etu.sprint.annotation.PostMapping;
import etu.sprint.annotation.RequestParameter;

@org.springframework.stereotype.Component
@AnnotationController("/vehicules")
public class VehiculePageController {
    @org.springframework.beans.factory.annotation.Autowired
    private VehiculeService vehiculeService;

    @org.springframework.beans.factory.annotation.Autowired
    private CarburantService carburantService;

    @GetMapping("")
    public ModelView listVehicules() {
        List<Vehicule> vehicules = vehiculeService.getAll();
        List<VehiculeDTO> vehiculeDTOs = vehicules.stream().map(VehiculeDTO::new).toList();
        ModelView mv = new ModelView();
        mv.setView("views/vehicule/list.html");
        mv.addItem("vehicules", vehiculeDTOs);
        return mv;
    }

    @GetMapping("/insert")
    public ModelView insertVehiculeForm() {
        ModelView mv = new ModelView();
        mv.setView("views/vehicule/insert.html");
        mv.addItem("carburants", carburantService.getAll());
        return mv;
    }

    @GetMapping("/detail")
    public ModelView detailVehicule(@RequestParameter("id") Long id) {
        ModelView mv = new ModelView();
        Vehicule vehicule = vehiculeService.findById(id);
        if (vehicule == null) {
            mv.setView("views/vehicule/list.html");
            mv.addItem("vehicules", vehiculeService.getAll().stream().map(VehiculeDTO::new).toList());
            mv.addItem("message", "Véhicule introuvable.");
            mv.addItem("messageType", "danger");
            return mv;
        }
        mv.setView("views/vehicule/detail.html");
        mv.addItem("vehicule", vehicule);
        return mv;
    }

    @PostMapping("/insert")
    public ModelView insertVehicule(
        @RequestParameter("ref") String ref,
        @RequestParameter("nbPlace") Integer nbPlace,
        @RequestParameter("carburantId") Long carburantId
    ) {
        ModelView mv = new ModelView();
        try {
            Carburant carburant = carburantService.findById(carburantId);
            if (carburant == null) {
                mv.setView("views/vehicule/insert.html");
                mv.addItem("carburants", carburantService.getAll());
                mv.addItem("message", "Erreur: carburant introuvable.");
                mv.addItem("messageType", "danger");
                return mv;
            }
            Vehicule vehicule = new Vehicule();
            vehicule.setRef(ref);
            vehicule.setNbPlace(nbPlace);
            vehicule.setCarburant(carburant);
            vehiculeService.save(vehicule);
            mv.setView("views/vehicule/list.html");
            mv.addItem("vehicules", vehiculeService.getAll().stream().map(VehiculeDTO::new).toList());
            mv.addItem("message", "Véhicule ajouté avec succès !");
            mv.addItem("messageType", "success");
        } catch (Exception e) {
            mv.setView("views/vehicule/insert.html");
            mv.addItem("carburants", carburantService.getAll());
            mv.addItem("message", "Erreur lors de l'ajout: " + e.getMessage());
            mv.addItem("messageType", "danger");
        }
        return mv;
    }

    @GetMapping("/edit")
    public ModelView editVehiculeForm(@RequestParameter("id") Long id) {
        ModelView mv = new ModelView();
        Vehicule vehicule = vehiculeService.findById(id);
        if (vehicule == null) {
            mv.setView("views/vehicule/list.html");
            mv.addItem("vehicules", vehiculeService.getAll().stream().map(VehiculeDTO::new).toList());
            mv.addItem("message", "Véhicule introuvable.");
            mv.addItem("messageType", "danger");
            return mv;
        }
        mv.setView("views/vehicule/edit.html");
        mv.addItem("vehicule", vehicule);
        mv.addItem("carburants", carburantService.getAll());
        return mv;
    }

    @PostMapping("/update")
    public ModelView updateVehicule(
        @RequestParameter("id") Long id,
        @RequestParameter("ref") String ref,
        @RequestParameter("nbPlace") Integer nbPlace,
        @RequestParameter("carburantId") Long carburantId
    ) {
        ModelView mv = new ModelView();
        try {
            Vehicule vehicule = vehiculeService.findById(id);
            if (vehicule == null) {
                mv.setView("views/vehicule/list.html");
                mv.addItem("vehicules", vehiculeService.getAll().stream().map(VehiculeDTO::new).toList());
                mv.addItem("message", "Véhicule introuvable.");
                mv.addItem("messageType", "danger");
                return mv;
            }

            Carburant carburant = carburantService.findById(carburantId);
            if (carburant == null) {
                mv.setView("views/vehicule/edit.html");
                mv.addItem("vehicule", vehicule);
                mv.addItem("carburants", carburantService.getAll());
                mv.addItem("message", "Erreur: carburant introuvable.");
                mv.addItem("messageType", "danger");
                return mv;
            }

            vehicule.setRef(ref);
            vehicule.setNbPlace(nbPlace);
            vehicule.setCarburant(carburant);
            vehiculeService.save(vehicule);
            
            mv.setView("views/vehicule/list.html");
            mv.addItem("vehicules", vehiculeService.getAll().stream().map(VehiculeDTO::new).toList());
            mv.addItem("message", "Véhicule mis à jour avec succès !");
            mv.addItem("messageType", "success");
        } catch (Exception e) {
            Vehicule vehicule = vehiculeService.findById(id);
            mv.setView("views/vehicule/edit.html");
            mv.addItem("vehicule", vehicule);
            mv.addItem("carburants", carburantService.getAll());
            mv.addItem("message", "Erreur lors de la mise à jour: " + e.getMessage());
            mv.addItem("messageType", "danger");
        }
        return mv;
    }

    @PostMapping("/delete")
    public ModelView deleteVehicule(@RequestParameter("id") Long id) {
        ModelView mv = new ModelView();
        try {
            vehiculeService.delete(id);
            mv.setView("views/vehicule/list.html");
            mv.addItem("vehicules", vehiculeService.getAll().stream().map(VehiculeDTO::new).toList());
            mv.addItem("message", "Véhicule supprimé avec succès !");
            mv.addItem("messageType", "success");
        } catch (Exception e) {
            mv.setView("views/vehicule/list.html");
            mv.addItem("vehicules", vehiculeService.getAll().stream().map(VehiculeDTO::new).toList());
            mv.addItem("message", "Erreur lors de la suppression: " + e.getMessage());
            mv.addItem("messageType", "danger");
        }
        return mv;
    }
}