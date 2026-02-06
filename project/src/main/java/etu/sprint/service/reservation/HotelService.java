package etu.sprint.service.reservation;

import etu.sprint.entity.reservation.Hotel;
import etu.sprint.repository.reservation.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;

    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    public Hotel findById(Integer id) {
        return hotelRepository.findById(id).orElse(null);
    }
}
