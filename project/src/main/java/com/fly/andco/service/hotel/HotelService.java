package com.fly.andco.service.hotel;

import com.fly.andco.model.hotel.Hotel;
import com.fly.andco.repository.hotel.HotelRepository;
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

    public Hotel save(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public void delete(Long id) {
        hotelRepository.deleteById(id);
    }
}
