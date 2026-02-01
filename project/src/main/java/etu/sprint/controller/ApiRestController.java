package etu.sprint.controller;

import etu.sprint.entity.*;
import etu.sprint.model.JsonResponse;
import etu.sprint.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiRestController {

    @Autowired
    private VoitureService voitureService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private TemoignageService temoignageService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private StatistiqueService statistiqueService;

    @Autowired
    private ReservationService reservationService;

    // ============== VOITURES ==============

    @GetMapping("/voitures")
    public ResponseEntity<JsonResponse> getVoitures() {
        List<Voiture> voitures = voitureService.findAll();
        return ResponseEntity.ok(new JsonResponse("success", 200, "Liste des voitures", voitures));
    }

    @GetMapping("/voitures/featured")
    public ResponseEntity<JsonResponse> getFeaturedVoitures() {
        List<Voiture> voitures = voitureService.findFeatured();
        return ResponseEntity.ok(new JsonResponse("success", 200, "Voitures en vedette", voitures));
    }

    @GetMapping("/voiture")
    public ResponseEntity<JsonResponse> getVoiture(@RequestParam Integer id) {
        return voitureService.findById(id)
                .map(v -> ResponseEntity.ok(new JsonResponse("success", 200, "Voiture trouvée", v)))
                .orElse(ResponseEntity.status(404).body(new JsonResponse("error", 404, "Voiture non trouvée", null)));
    }

    // ============== SERVICES ==============

    @GetMapping("/services")
    public ResponseEntity<JsonResponse> getServices() {
        List<Service> services = serviceService.findAllActive();
        return ResponseEntity.ok(new JsonResponse("success", 200, "Liste des services", services));
    }

    // ============== TEMOIGNAGES ==============

    @GetMapping("/temoignages")
    public ResponseEntity<JsonResponse> getTemoignages() {
        List<Temoignage> temoignages = temoignageService.findAllActive();
        return ResponseEntity.ok(new JsonResponse("success", 200, "Liste des témoignages", temoignages));
    }

    // ============== BLOGS ==============

    @GetMapping("/blogs")
    public ResponseEntity<JsonResponse> getBlogs() {
        List<Blog> blogs = blogService.findAllActive();
        return ResponseEntity.ok(new JsonResponse("success", 200, "Liste des articles", blogs));
    }

    @GetMapping("/blogs/recent")
    public ResponseEntity<JsonResponse> getRecentBlogs() {
        List<Blog> blogs = blogService.findRecent(3);
        return ResponseEntity.ok(new JsonResponse("success", 200, "Articles récents", blogs));
    }

    @GetMapping("/blog")
    public ResponseEntity<JsonResponse> getBlog(@RequestParam Integer id) {
        return blogService.findById(id)
                .map(b -> ResponseEntity.ok(new JsonResponse("success", 200, "Article trouvé", b)))
                .orElse(ResponseEntity.status(404).body(new JsonResponse("error", 404, "Article non trouvé", null)));
    }

    // ============== STATISTIQUES ==============

    @GetMapping("/statistiques")
    public ResponseEntity<JsonResponse> getStatistiques() {
        List<Statistique> stats = statistiqueService.findAll();
        return ResponseEntity.ok(new JsonResponse("success", 200, "Statistiques", stats));
    }

    // ============== RESERVATIONS ==============

    @PostMapping("/reservations")
    public ResponseEntity<JsonResponse> createReservation(@RequestBody Reservation reservation) {
        Reservation saved = reservationService.save(reservation);
        return ResponseEntity.ok(new JsonResponse("success", 201, "Réservation créée", saved));
    }

    @GetMapping("/reservations")
    public ResponseEntity<JsonResponse> getReservations() {
        List<Reservation> reservations = reservationService.findAll();
        return ResponseEntity.ok(new JsonResponse("success", 200, "Liste des réservations", reservations));
    }
}
