package etu.sprint.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "temoignages")
public class Temoignage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 100)
    private String poste;

    @Column(length = 255)
    private String photo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String commentaire;

    @Column(columnDefinition = "INTEGER DEFAULT 5")
    private Integer note = 5;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean actif = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Temoignage() {}

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
