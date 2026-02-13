package com.vroomer.dashboard.repository.vehicule;

import com.vroomer.dashboard.model.vehicule.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
}