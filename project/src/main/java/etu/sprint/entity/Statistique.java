package etu.sprint.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistiques")
public class Statistique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String cle;

    @Column(nullable = false)
    private Integer valeur;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(length = 100)
    private String icone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Statistique() {}

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCle() { return cle; }
    public void setCle(String cle) { this.cle = cle; }

    public Integer getValeur() { return valeur; }
    public void setValeur(Integer valeur) { this.valeur = valeur; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getIcone() { return icone; }
    public void setIcone(String icone) { this.icone = icone; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
