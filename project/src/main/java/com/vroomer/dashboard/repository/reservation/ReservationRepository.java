package com.vrommer.dashboard.repository.reservation;

import com.vrommer.dashboard.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
