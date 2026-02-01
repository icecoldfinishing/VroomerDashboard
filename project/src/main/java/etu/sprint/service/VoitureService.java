package etu.sprint.service;

import etu.sprint.entity.Voiture;
import etu.sprint.repository.VoitureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoitureService {

    @Autowired
    private VoitureRepository voitureRepository;

    public List<Voiture> findAll() {
        return voitureRepository.findByDisponibleTrue();
    }

    public List<Voiture> findFeatured() {
        return voitureRepository.findByDisponibleTrueAndFeaturedTrue();
    }

    public Optional<Voiture> findById(Integer id) {
        return voitureRepository.findById(id);
    }

    public List<Voiture> findByMarque(Integer marqueId) {
        return voitureRepository.findByMarqueId(marqueId);
    }

    public List<Voiture> findByCategorie(Integer categorieId) {
        return voitureRepository.findByCategorieId(categorieId);
    }

    public Voiture save(Voiture voiture) {
        return voitureRepository.save(voiture);
    }

    public void deleteById(Integer id) {
        voitureRepository.deleteById(id);
    }
}
