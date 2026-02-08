package com.vroomer.dashboard.controller.reservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;

import java.util.List;
import com.vroomer.dashboard.dto.ReservationDTO;

import com.vroomer.dashboard.model.reservation.Reservation;
import com.vroomer.dashboard.service.reservation.ReservationService;

import etu.sprint.model.ModelView;
import etu.sprint.annotation.AnnotationController;
import etu.sprint.annotation.GetMapping;
import etu.sprint.annotation.RequestParameter;
import etu.sprint.annotation.RestAPI;


@org.springframework.stereotype.Component
@AnnotationController("/api")
public class ReservationApiController {
    @org.springframework.beans.factory.annotation.Autowired
    private ReservationService reservationService;
    

    @RestAPI
    @GetMapping("/reservations")
    public List<ReservationDTO> listReservationsApi() {
        List<Reservation> reservations = reservationService.getAll();
        List<ReservationDTO> dtos = new java.util.ArrayList<>();
        for (Reservation r : reservations) {
            dtos.add(new ReservationDTO(r));
        }
        return dtos;
    }

    @RestAPI
    @GetMapping("/reservations/filter")
    public List<ReservationDTO> filterReservationsBetweenDates(
        @etu.sprint.annotation.RequestParameter("start") String start,
        @etu.sprint.annotation.RequestParameter("end") String end
    ) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        java.time.LocalDate startDate = java.time.LocalDate.parse(start, formatter);
        java.time.LocalDate endDate = java.time.LocalDate.parse(end, formatter);
        java.time.LocalDateTime startDateTime = startDate.atStartOfDay();
        java.time.LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<Reservation> reservations = reservationService.findBetweenDates(startDateTime, endDateTime);
        List<ReservationDTO> dtos = new java.util.ArrayList<>();
        for (Reservation r : reservations) {
            dtos.add(new ReservationDTO(r));
        }
        return dtos;
    }
    
}
