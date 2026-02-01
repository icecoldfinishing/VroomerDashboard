package etu.sprint.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "voitures")
public class Voiture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String nom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "marque_id")
    private Marque marque;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @Column(name = "prix_jour", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixJour;

    @Column(length = 255)
    private String image;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer annee;

    @Column(columnDefinition = "INTEGER DEFAULT 5")
    private Integer places = 5;

    @Column(columnDefinition = "INTEGER DEFAULT 4")
    private Integer portes = 4;

    @Column(length = 50)
    private String transmission = "Automatic";

    @Column(length = 50)
    private String carburant = "Diesel";

    @Column(length = 50)
    private String kilometrage = "Unlimited";

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean disponible = true;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean featured = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Voiture() {}

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Marque getMarque() { return marque; }
    public void setMarque(Marque marque) { this.marque = marque; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public BigDecimal getPrixJour() { return prixJour; }
    public void setPrixJour(BigDecimal prixJour) { this.prixJour = prixJour; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public Integer getPlaces() { return places; }
    public void setPlaces(Integer places) { this.places = places; }

    public Integer getPortes() { return portes; }
    public void setPortes(Integer portes) { this.portes = portes; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public String getCarburant() { return carburant; }
    public void setCarburant(String carburant) { this.carburant = carburant; }

    public String getKilometrage() { return kilometrage; }
    public void setKilometrage(String kilometrage) { this.kilometrage = kilometrage; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
