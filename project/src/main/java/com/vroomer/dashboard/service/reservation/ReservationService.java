
package com.vroomer.dashboard.service.reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vroomer.dashboard.model.reservation.Reservation;
import com.vroomer.dashboard.repository.reservation.ReservationRepository;


import java.util.List;


@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;


    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }
}
