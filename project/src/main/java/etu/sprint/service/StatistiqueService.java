package etu.sprint.service;

import etu.sprint.entity.Statistique;
import etu.sprint.repository.StatistiqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatistiqueService {

    @Autowired
    private StatistiqueRepository statistiqueRepository;

    public List<Statistique> findAll() {
        return statistiqueRepository.findAll();
    }

    public Optional<Statistique> findById(Integer id) {
        return statistiqueRepository.findById(id);
    }

    public Optional<Statistique> findByCle(String cle) {
        return statistiqueRepository.findByCle(cle);
    }

    public Statistique save(Statistique statistique) {
        return statistiqueRepository.save(statistique);
    }

    public void deleteById(Integer id) {
        statistiqueRepository.deleteById(id);
    }
}
