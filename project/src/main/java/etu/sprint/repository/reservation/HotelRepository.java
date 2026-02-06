package etu.sprint.repository.reservation;

import etu.sprint.entity.reservation.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
}
