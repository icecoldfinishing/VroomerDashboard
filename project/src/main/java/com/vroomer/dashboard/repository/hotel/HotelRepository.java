package com.vroomer.dashboard.repository.hotel;

import com.vroomer.dashboard.model.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
