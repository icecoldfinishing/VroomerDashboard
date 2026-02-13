package com.vroomer.dashboard.repository.carburant;

import com.vroomer.dashboard.model.carburant.Carburant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarburantRepository extends JpaRepository<Carburant, Long> {
}