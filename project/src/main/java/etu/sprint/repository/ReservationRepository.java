package etu.sprint.repository;

import etu.sprint.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
    List<Reservation> findByStatut(String statut);
    
    List<Reservation> findByVoitureId(Integer voitureId);
    
    List<Reservation> findByEmailClient(String emailClient);
}
