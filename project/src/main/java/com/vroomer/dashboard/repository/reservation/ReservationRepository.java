package com.vroomer.dashboard.repository.reservation;

import com.vroomer.dashboard.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
