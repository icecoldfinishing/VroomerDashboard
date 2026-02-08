package com.vroomer.dashboard.service.hotel;

import com.vroomer.dashboard.model.hotel.Hotel;
import com.vroomer.dashboard.repository.hotel.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;

    public List<Hotel> getAll() {
        return hotelRepository.findAll();
    }

    public Hotel findById(Long id) {
        return hotelRepository.findById(id).orElse(null);
    }

    public Hotel save(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public void delete(Long id) {
        hotelRepository.deleteById(id);
    }
}
