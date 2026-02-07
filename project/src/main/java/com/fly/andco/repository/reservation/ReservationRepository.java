package com.fly.andco.repository.reservation;

import com.fly.andco.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
