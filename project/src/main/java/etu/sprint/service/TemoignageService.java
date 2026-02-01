package etu.sprint.service;

import etu.sprint.entity.Temoignage;
import etu.sprint.repository.TemoignageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemoignageService {

    @Autowired
    private TemoignageRepository temoignageRepository;

    public List<Temoignage> findAllActive() {
        return temoignageRepository.findByActifTrue();
    }

    public List<Temoignage> findAll() {
        return temoignageRepository.findAll();
    }

    public Optional<Temoignage> findById(Integer id) {
        return temoignageRepository.findById(id);
    }

    public Temoignage save(Temoignage temoignage) {
        return temoignageRepository.save(temoignage);
    }

    public void deleteById(Integer id) {
        temoignageRepository.deleteById(id);
    }
}
