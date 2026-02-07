package com.vroomer.dashboard.repository.client;

import com.vroomer.dashboard.model.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
