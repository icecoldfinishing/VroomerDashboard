package com.vroomer.dashboard.model.token;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token")
public class ApiToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "datetime_exp", nullable = false)
    private LocalDateTime datetimeExp;

    public ApiToken() {
    }

    public ApiToken(String token, LocalDateTime datetimeExp) {
        this.token = token;
        this.datetimeExp = datetimeExp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getDatetimeExp() {
        return datetimeExp;
    }

    public void setDatetimeExp(LocalDateTime datetimeExp) {
        this.datetimeExp = datetimeExp;
    }
}
