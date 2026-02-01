package etu.sprint.repository;

import etu.sprint.entity.Voiture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoitureRepository extends JpaRepository<Voiture, Integer> {
    
    List<Voiture> findByDisponibleTrue();
    
    List<Voiture> findByDisponibleTrueAndFeaturedTrue();
    
    List<Voiture> findByMarqueId(Integer marqueId);
    
    List<Voiture> findByCategorieId(Integer categorieId);
}
