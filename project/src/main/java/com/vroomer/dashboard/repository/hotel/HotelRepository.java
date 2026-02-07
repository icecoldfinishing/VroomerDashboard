package com.vrommer.dashboard.repository.hotel;

import com.vrommer.dashboard.model.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
