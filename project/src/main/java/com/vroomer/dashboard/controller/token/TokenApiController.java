package com.vroomer.dashboard.controller.token;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vroomer.dashboard.model.token.ApiToken;
import com.vroomer.dashboard.service.token.ApiTokenService;

import etu.sprint.annotation.AnnotationController;
import etu.sprint.annotation.GetMapping;
import etu.sprint.annotation.PostMapping;
import etu.sprint.annotation.RequestParameter;
import etu.sprint.annotation.RestAPI;
import etu.sprint.annotation.Token;


@org.springframework.stereotype.Component
@AnnotationController("/api/tokens")
public class TokenApiController {

    @org.springframework.beans.factory.annotation.Autowired
    private ApiTokenService apiTokenService;

    /**
     * GET /api/tokens - Liste tous les tokens (protégé par token)
     */
    @Token
    @RestAPI
    @GetMapping("")
    public List<Map<String, Object>> listTokens() {
        List<ApiToken> tokens = apiTokenService.getAll();
        return tokens.stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("token", t.getToken());
            map.put("datetimeExp", t.getDatetimeExp().toString());
            map.put("valid", t.getDatetimeExp().isAfter(LocalDateTime.now()));
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * POST /api/tokens/generate - Génère un nouveau token (protégé par token)
     * Paramètre optionnel: tokenValue (sinon auto-généré)
     * Paramètre optionnel: daysValid (par défaut 365 jours)
     */
    @Token
    @RestAPI
    @PostMapping("/generate")
    public Map<String, Object> generateToken(
        @RequestParameter("tokenValue") String tokenValue,
        @RequestParameter("daysValid") Integer daysValid
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            int days = (daysValid != null) ? daysValid : 365;
            LocalDateTime expiration = LocalDateTime.now().plusDays(days);
            
            ApiToken newToken;
            if (tokenValue != null && !tokenValue.isEmpty()) {
                newToken = apiTokenService.generateToken(tokenValue, expiration);
            } else {
                newToken = apiTokenService.generateToken(expiration);
            }
            result.put("id", newToken.getId());
            result.put("token", newToken.getToken());
            result.put("datetimeExp", newToken.getDatetimeExp().toString());
            result.put("message", "Token généré avec succès");
        } catch (Exception e) {
            result.put("error", "Erreur lors de la génération du token: " + e.getMessage());
        }
        return result;
    }

    /**
     * GET /api/tokens/validate - Vérifie si le token dans le header est valide
     * Cet endpoint n'est PAS protégé par @Token pour permettre de tester la validité
     */
    @RestAPI
    @GetMapping("/validate")
    public Map<String, Object> validateToken(
        jakarta.servlet.http.HttpServletRequest request
    ) {
        Map<String, Object> result = new HashMap<>();
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            result.put("valid", false);
            result.put("message", "Aucun token fourni dans le header Authorization");
            return result;
        }
        boolean valid = apiTokenService.isTokenValid(authHeader);
        result.put("valid", valid);
        result.put("token", authHeader);
        if (valid) {
            result.put("message", "Token valide");
        } else {
            result.put("message", "Token invalide ou expiré");
        }
        return result;
    }
}
