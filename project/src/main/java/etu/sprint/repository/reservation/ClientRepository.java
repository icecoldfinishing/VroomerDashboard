package etu.sprint.repository.reservation;

import etu.sprint.entity.reservation.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
