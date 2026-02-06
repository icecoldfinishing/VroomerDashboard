package etu.sprint.entity.reservation;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idclient")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "idhotel")
    private Hotel hotel;

    private Integer nbPassager;
    private LocalDateTime dateheure;

    // Getters et setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public Integer getNbPassager() { return nbPassager; }
    public void setNbPassager(Integer nbPassager) { this.nbPassager = nbPassager; }

    public LocalDateTime getDateheure() { return dateheure; }
    public void setDateheure(LocalDateTime dateheure) { this.dateheure = dateheure; }
}
