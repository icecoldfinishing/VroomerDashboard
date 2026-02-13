package com.vroomer.dashboard.service.carburant;

import com.vroomer.dashboard.model.carburant.Carburant;
import com.vroomer.dashboard.repository.carburant.CarburantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarburantService {
    @Autowired
    private CarburantRepository carburantRepository;

    public List<Carburant> getAll() {
        return carburantRepository.findAll();
    }

    public Carburant findById(Long id) {
        return carburantRepository.findById(id).orElse(null);
    }

    public Carburant save(Carburant carburant) {
        return carburantRepository.save(carburant);
    }

    public void delete(Long id) {
        carburantRepository.deleteById(id);
    }
}