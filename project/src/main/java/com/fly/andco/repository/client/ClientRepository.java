package com.fly.andco.repository.client;

import com.fly.andco.model.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
