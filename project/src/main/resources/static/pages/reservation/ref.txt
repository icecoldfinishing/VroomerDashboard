package etu.sprint.controller;

import etu.sprint.annotation.AnnotationController;
import etu.sprint.annotation.PostMapping;
import etu.sprint.annotation.GetMapping;
import etu.sprint.annotation.RequestParameter;
import etu.sprint.model.Employe;
import etu.sprint.model.ModelView;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@AnnotationController("/employers")
public class EmployeController {

    private static final List<Employe> employes = new ArrayList<>();
    private static int nextId = 1;

    static {
        employes.add(new Employe(nextId++, "Jean Dupont", 2500.0, Arrays.asList("Java", "SQL")));
        employes.add(new Employe(nextId++, "Marie Martin", 2800.50, Arrays.asList("Python", "Cloud")));
        employes.add(new Employe(nextId++, "Paul Durand", 3200.0, Arrays.asList("Frontend", "UI/UX")));
    }
    
    @GetMapping("/")
    public ModelView getEmployeList() {
        ModelView mv = new ModelView();
        mv.addItem("employeList", employes);
        mv.setView("/views/employe-list.jsp");
        return mv;
    }

    @GetMapping("/add")
    public ModelView showAddEmployeForm() {
        ModelView mv = new ModelView();
        mv.setView("/views/employe-form.jsp");
        return mv;
    }

    @PostMapping("/add")
    public ModelView addEmploye(Employe employe) {
        employe.setId(nextId++);
        employes.add(employe);

        ModelView mv = new ModelView();
        mv.addItem("successMessage", "Employe " + employe.getNom() + " added successfully!");
        mv.addItem("employeList", employes);
        mv.setView("/views/employe-list.jsp");
        return mv;
    }

    @PostMapping("/map-add")
    public ModelView addEmployeWithMap(Map<String, Object> formData) {
        String nom = (String) formData.get("nom");
        double salaire = Double.parseDouble((String) formData.get("salaire"));
        List<String> skills = (List<String>) formData.get("skills"); // Checkbox values will be a List<String>

        Employe newEmploye = new Employe(nextId++, nom, salaire, skills);
        employes.add(newEmploye);

        ModelView mv = new ModelView();
        mv.addItem("successMessage", "Employe " + newEmploye.getNom() + " added successfully with map! Skills: " + skills);
        mv.addItem("employeList", employes);
        mv.setView("/views/employe-form-map.jsp");
        return mv;
    }
}
