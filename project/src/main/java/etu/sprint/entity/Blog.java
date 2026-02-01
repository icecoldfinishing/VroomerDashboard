package etu.sprint.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 300)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    @Column(length = 500)
    private String extrait;

    @Column(length = 255)
    private String image;

    @Column(length = 100)
    private String auteur = "Admin";

    @Column(name = "commentaires_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer commentairesCount = 0;

    @Column(name = "date_publication")
    private LocalDate datePublication;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean actif = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Blog() {}

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getExtrait() { return extrait; }
    public void setExtrait(String extrait) { this.extrait = extrait; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public Integer getCommentairesCount() { return commentairesCount; }
    public void setCommentairesCount(Integer commentairesCount) { this.commentairesCount = commentairesCount; }

    public LocalDate getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDate datePublication) { this.datePublication = datePublication; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
