package com.fly.andco.repository.hotel;

import com.fly.andco.model.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
