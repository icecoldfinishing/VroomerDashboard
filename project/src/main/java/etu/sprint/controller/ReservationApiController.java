package etu.sprint.controller;

import etu.sprint.entity.reservation.Client;
import etu.sprint.entity.reservation.Hotel;
import etu.sprint.entity.reservation.Reservation;
import etu.sprint.service.reservation.ClientService;
import etu.sprint.service.reservation.HotelService;
import etu.sprint.service.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReservationApiController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/clients")
    public List<Client> getAllClients() {
        return clientService.findAll();
    }

    @GetMapping("/hotels")
    public List<Hotel> getAllHotels() {
        return hotelService.findAll();
    }

    @PostMapping("/reservations")
    public ResponseEntity<?> insertReservation(@RequestBody Map<String, String> data) {
        try {
            Integer idClient = Integer.parseInt(data.get("idClient"));
            Integer nbPassager = Integer.parseInt(data.get("NbPassager"));
            LocalDateTime dateHeure = LocalDateTime.parse(data.get("dateheure"));
            Integer idHotel = Integer.parseInt(data.get("idHotel"));

            Client client = clientService.findById(idClient);
            Hotel hotel = hotelService.findById(idHotel);

            if (client == null || hotel == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Client ou hotel introuvable."));
            }

            Reservation res = new Reservation();
            res.setClient(client);
            res.setHotel(hotel);
            res.setNbPassager(nbPassager);
            res.setDateheure(dateHeure);

            reservationService.save(res);
            return ResponseEntity.ok(Map.of("message", "Reservation ajoutee avec succes!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Erreur lors de l'insertion: " + e.getMessage()));
        }
    }
}
