package etu.sprint.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "voiture_id")
    private Voiture voiture;

    @Column(name = "nom_client", nullable = false, length = 200)
    private String nomClient;

    @Column(name = "email_client", length = 200)
    private String emailClient;

    @Column(length = 50)
    private String telephone;

    @Column(name = "lieu_depart", length = 300)
    private String lieuDepart;

    @Column(name = "lieu_arrivee", length = 300)
    private String lieuArrivee;

    @Column(name = "date_depart", nullable = false)
    private LocalDate dateDepart;

    @Column(name = "date_retour", nullable = false)
    private LocalDate dateRetour;

    @Column(name = "heure_depart")
    private LocalTime heureDepart;

    @Column(length = 50)
    private String statut = "pending";

    @Column(name = "prix_total", precision = 10, scale = 2)
    private BigDecimal prixTotal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Reservation() {}

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Voiture getVoiture() { return voiture; }
    public void setVoiture(Voiture voiture) { this.voiture = voiture; }

    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }

    public String getEmailClient() { return emailClient; }
    public void setEmailClient(String emailClient) { this.emailClient = emailClient; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getLieuDepart() { return lieuDepart; }
    public void setLieuDepart(String lieuDepart) { this.lieuDepart = lieuDepart; }

    public String getLieuArrivee() { return lieuArrivee; }
    public void setLieuArrivee(String lieuArrivee) { this.lieuArrivee = lieuArrivee; }

    public LocalDate getDateDepart() { return dateDepart; }
    public void setDateDepart(LocalDate dateDepart) { this.dateDepart = dateDepart; }

    public LocalDate getDateRetour() { return dateRetour; }
    public void setDateRetour(LocalDate dateRetour) { this.dateRetour = dateRetour; }

    public LocalTime getHeureDepart() { return heureDepart; }
    public void setHeureDepart(LocalTime heureDepart) { this.heureDepart = heureDepart; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public BigDecimal getPrixTotal() { return prixTotal; }
    public void setPrixTotal(BigDecimal prixTotal) { this.prixTotal = prixTotal; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
