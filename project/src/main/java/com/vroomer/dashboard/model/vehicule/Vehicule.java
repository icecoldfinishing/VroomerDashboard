package com.vroomer.dashboard.model.vehicule;

import jakarta.persistence.*;
import com.vroomer.dashboard.model.carburant.Carburant;

@Entity
@Table(name = "vehicule")
public class Vehicule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ref;

    @Column(name = "nb_place")
    private Integer nbPlace;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_carburant")
    private Carburant carburant;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
    public Integer getNbPlace() { return nbPlace; }
    public void setNbPlace(Integer nbPlace) { this.nbPlace = nbPlace; }
    public Carburant getCarburant() { return carburant; }
    public void setCarburant(Carburant carburant) { this.carburant = carburant; }
}