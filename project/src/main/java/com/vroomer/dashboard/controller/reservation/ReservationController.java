package com.vroomer.dashboard.controller.reservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;

import java.util.List;
import com.vroomer.dashboard.dto.ReservationDTO;

import com.vroomer.dashboard.model.hotel.Hotel;
import com.vroomer.dashboard.model.client.Client;
import com.vroomer.dashboard.model.reservation.Reservation;
import com.vroomer.dashboard.service.reservation.ReservationService;

import etu.sprint.model.ModelView;
import etu.sprint.annotation.AnnotationController;
import etu.sprint.annotation.GetMapping;
import etu.sprint.annotation.PostMapping;
import etu.sprint.annotation.RequestParameter;
import etu.sprint.annotation.RestAPI;


@org.springframework.stereotype.Component
@AnnotationController("/reservations")
public class ReservationController {
    @org.springframework.beans.factory.annotation.Autowired
    private ReservationService reservationService;
    
    @org.springframework.beans.factory.annotation.Autowired
    private com.vroomer.dashboard.service.hotel.HotelService hotelService;
    
    @org.springframework.beans.factory.annotation.Autowired
    private com.vroomer.dashboard.service.client.ClientService clientService;
    


    @RestAPI
    @GetMapping("/api")
    public List<ReservationDTO> listReservationsApi() {
        List<Reservation> reservations = reservationService.getAll();
        List<ReservationDTO> dtos = new java.util.ArrayList<>();
        for (Reservation r : reservations) {
            dtos.add(new ReservationDTO(r));
        }
        return dtos;
    }
    @GetMapping("/list")
    public ModelView listReservations() {
        List<Reservation> reservations = reservationService.getAll();
        ModelView mv = new ModelView();
        mv.setView("views/reservation/list.html"); 
        mv.addItem("reservations", reservations);
        return mv;
    }
    
    @GetMapping("/insert")
    public ModelView insertReservationForm() {
        ModelView mv = new ModelView();
        mv.setView("views/reservation/insert.html");
        mv.addItem("hotels", hotelService.getAll());
        mv.addItem("clients", clientService.getAll());
        return mv;
    }
    @PostMapping("/insert")
    public ModelView insertReservation(
        @RequestParameter("clientId") Long clientId,
        @RequestParameter("hotelId") Long hotelId,
        @RequestParameter("nbPassager") Integer nbPassager,
        @RequestParameter("dateheure") String dateheureStr
    ) {
        ModelView mv = new ModelView();
        try {
            Client client = clientService.findById(clientId);
            Hotel hotel = hotelService.findById(hotelId);
            if (client == null || hotel == null) {
                mv.setView("views/reservation/insert.html");
                mv.addItem("hotels", hotelService.getAll());
                mv.addItem("clients", clientService.getAll());
                mv.addItem("message", "Erreur: client ou hôtel introuvable.");
                mv.addItem("messageType", "danger");
                return mv;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime dateheure = LocalDateTime.parse(dateheureStr, formatter);
            Reservation reservation = new Reservation();
            reservation.setClient(client);
            reservation.setHotel(hotel);
            reservation.setNbPassager(nbPassager);
            reservation.setDateheure(dateheure);
            reservationService.save(reservation);
            mv.setView("views/reservation/insert.html");
            mv.addItem("hotels", hotelService.getAll());
            mv.addItem("clients", clientService.getAll());
            mv.addItem("message", "Réservation ajoutée avec succès !");
            mv.addItem("messageType", "success");
        } catch (DateTimeParseException e) {
            mv.setView("views/reservation/insert.html");
            mv.addItem("hotels", hotelService.getAll());
            mv.addItem("clients", clientService.getAll());
            mv.addItem("message", "Erreur de format de date/heure.");
            mv.addItem("messageType", "danger");
        } catch (Exception e) {
            mv.setView("views/reservation/insert.html");
            mv.addItem("hotels", hotelService.getAll());
            mv.addItem("clients", clientService.getAll());
            mv.addItem("message", "Erreur lors de l'ajout: " + e.getMessage());
            mv.addItem("messageType", "danger");
        }
        return mv;
    }
}
