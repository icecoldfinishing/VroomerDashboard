package etu.sprint.repository;

import etu.sprint.entity.Statistique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatistiqueRepository extends JpaRepository<Statistique, Integer> {
    
    Optional<Statistique> findByCle(String cle);
}
