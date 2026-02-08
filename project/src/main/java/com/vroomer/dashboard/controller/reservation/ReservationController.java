package com.vroomer.dashboard.controller.reservation;

import com.vroomer.dashboard.model.reservation.Reservation;
import com.vroomer.dashboard.service.reservation.ReservationService;
import etu.sprint.model.ModelView;
import etu.sprint.annotation.AnnotationController;
import etu.sprint.annotation.GetMapping;

import java.util.List;

@org.springframework.stereotype.Component
@AnnotationController("/reservations")
public class ReservationController {
    @org.springframework.beans.factory.annotation.Autowired
    private ReservationService reservationService;


    @GetMapping("/list")
    public ModelView listReservations() {
        List<Reservation> reservations = reservationService.getAll();
        ModelView mv = new ModelView();
        mv.setView("views/reservation/list.html"); 
        mv.addItem("reservations", reservations);
        return mv;
    }
}
