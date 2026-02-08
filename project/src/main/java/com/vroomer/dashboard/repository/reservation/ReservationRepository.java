package com.vroomer.dashboard.repository.reservation;

import com.vroomer.dashboard.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	List<Reservation> findByDateheureBetween(LocalDateTime start, LocalDateTime end);
}
