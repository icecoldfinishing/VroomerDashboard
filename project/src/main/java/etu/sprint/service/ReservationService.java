package etu.sprint.service;

import etu.sprint.entity.Reservation;
import etu.sprint.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> findById(Integer id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> findByStatut(String statut) {
        return reservationRepository.findByStatut(statut);
    }

    public List<Reservation> findByVoiture(Integer voitureId) {
        return reservationRepository.findByVoitureId(voitureId);
    }

    public List<Reservation> findByEmail(String email) {
        return reservationRepository.findByEmailClient(email);
    }

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteById(Integer id) {
        reservationRepository.deleteById(id);
    }
}
