package com.vroomer.dashboard.service.vehicule;

import com.vroomer.dashboard.model.vehicule.Vehicule;
import com.vroomer.dashboard.repository.vehicule.VehiculeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculeService {
    @Autowired
    private VehiculeRepository vehiculeRepository;

    public List<Vehicule> getAll() {
        return vehiculeRepository.findAll();
    }

    public Vehicule findById(Long id) {
        return vehiculeRepository.findById(id).orElse(null);
    }

    public Vehicule save(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    public void delete(Long id) {
        vehiculeRepository.deleteById(id);
    }
}