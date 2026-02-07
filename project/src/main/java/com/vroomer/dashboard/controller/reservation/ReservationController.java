package com.vrommer.dashboard.controller.reservation;

import com.vrommer.dashboard.model.reservation.Reservation;
import com.vrommer.dashboard.service.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping("/list")
    public String listReservations(Model model) {
        List<Reservation> reservations = reservationService.getAll();
        model.addAttribute("reservations", reservations);
        return "views/reservation/list";
    }
}
