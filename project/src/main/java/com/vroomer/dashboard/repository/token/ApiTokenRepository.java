package com.vroomer.dashboard.repository.token;

import com.vroomer.dashboard.model.token.ApiToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiTokenRepository extends JpaRepository<ApiToken, Long> {
    Optional<ApiToken> findByToken(String token);
}
