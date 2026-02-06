package etu.sprint.service.reservation;

import etu.sprint.entity.reservation.Client;
import etu.sprint.repository.reservation.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(Integer id) {
        return clientRepository.findById(id).orElse(null);
    }
}
