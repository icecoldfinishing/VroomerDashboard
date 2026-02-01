package etu.sprint.repository;

import etu.sprint.entity.Temoignage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemoignageRepository extends JpaRepository<Temoignage, Integer> {
    
    List<Temoignage> findByActifTrue();
}
