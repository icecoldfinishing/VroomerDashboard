package com.vroomer.dashboard.service.token;

import com.vroomer.dashboard.model.token.ApiToken;
import com.vroomer.dashboard.repository.token.ApiTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiTokenService {

    @Autowired
    private ApiTokenRepository apiTokenRepository;

    /**
     * Vérifie si un token est valide :
     * 1. Le token existe dans la table token
     * 2. La date d'expiration n'est pas dépassée
     */
    public boolean isTokenValid(String tokenValue) {
        if (tokenValue == null || tokenValue.isEmpty()) {
            return false;
        }
        Optional<ApiToken> tokenOpt = apiTokenRepository.findByToken(tokenValue);
        if (tokenOpt.isEmpty()) {
            return false;
        }
        ApiToken apiToken = tokenOpt.get();
        return apiToken.getDatetimeExp().isAfter(LocalDateTime.now());
    }

    /**
     * Génère un nouveau token avec une date d'expiration donnée
     */
    public ApiToken generateToken(LocalDateTime expirationDate) {
        String tokenValue = "FRONT-KEY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ApiToken apiToken = new ApiToken(tokenValue, expirationDate);
        return apiTokenRepository.save(apiToken);
    }

    /**
     * Génère un token avec une valeur personnalisée
     */
    public ApiToken generateToken(String tokenValue, LocalDateTime expirationDate) {
        ApiToken apiToken = new ApiToken(tokenValue, expirationDate);
        return apiTokenRepository.save(apiToken);
    }

    /**
     * Liste tous les tokens
     */
    public List<ApiToken> getAll() {
        return apiTokenRepository.findAll();
    }

    /**
     * Supprime un token par ID
     */
    public void delete(Long id) {
        apiTokenRepository.deleteById(id);
    }

    /**
     * Trouve un token par sa valeur
     */
    public Optional<ApiToken> findByToken(String tokenValue) {
        return apiTokenRepository.findByToken(tokenValue);
    }
}
