package com.vroomer.dashboard.dto;

import com.vroomer.dashboard.model.reservation.Reservation;

public class ReservationDTO {
    public Long id;
    public String clientNom;
    public String hotelNom;
    public Integer nbPassager;
    public String dateheure;

    public ReservationDTO(Reservation r) {
        this.id = r.getId();
        this.clientNom = (r.getClient() != null) ? r.getClient().getNom() : null;
        this.hotelNom = (r.getHotel() != null) ? r.getHotel().getNom() : null;
        this.nbPassager = r.getNbPassager();
        this.dateheure = (r.getDateheure() != null) ? r.getDateheure().toString() : null;
    }
}
