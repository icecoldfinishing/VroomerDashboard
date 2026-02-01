package etu.sprint.service;

import etu.sprint.entity.Service;
import etu.sprint.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<Service> findAllActive() {
        return serviceRepository.findByActifTrueOrderByOrdreAsc();
    }

    public List<Service> findAll() {
        return serviceRepository.findAll();
    }

    public Optional<Service> findById(Integer id) {
        return serviceRepository.findById(id);
    }

    public Service save(Service service) {
        return serviceRepository.save(service);
    }

    public void deleteById(Integer id) {
        serviceRepository.deleteById(id);
    }
}
