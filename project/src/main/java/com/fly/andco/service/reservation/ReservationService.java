package com.fly.andco.service.reservation;

import com.fly.andco.model.reservation.Reservation;
import com.fly.andco.repository.reservation.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
