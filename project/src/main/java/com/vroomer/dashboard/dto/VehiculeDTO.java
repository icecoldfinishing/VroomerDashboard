package com.vroomer.dashboard.dto;

import com.vroomer.dashboard.model.vehicule.Vehicule;

public class VehiculeDTO {
    public Long id;
    public String ref;
    public Integer nbPlace;
    public String carburantType;

    public VehiculeDTO(Vehicule v) {
        this.id = v.getId();
        this.ref = v.getRef();
        this.nbPlace = v.getNbPlace();
        this.carburantType = (v.getCarburant() != null) ? v.getCarburant().getType() : null;
    }
}