package com.vrommer.dashboard.repository.client;

import com.vrommer.dashboard.model.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
