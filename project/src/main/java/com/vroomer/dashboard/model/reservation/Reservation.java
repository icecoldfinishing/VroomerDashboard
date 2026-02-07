package com.vrommer.dashboard.model.reservation;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.vrommer.dashboard.model.client.Client;
import com.vrommer.dashboard.model.hotel.Hotel;

@Entity
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_client")
	private Client client;

	@Column(name = "nb_passager")
	private Integer nbPassager;

	private LocalDateTime dateheure;

	@ManyToOne
	@JoinColumn(name = "id_hotel")
	private Hotel hotel;

	// Getters and setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Client getClient() { return client; }
	public void setClient(Client client) { this.client = client; }
	public Integer getNbPassager() { return nbPassager; }
	public void setNbPassager(Integer nbPassager) { this.nbPassager = nbPassager; }
	public LocalDateTime getDateheure() { return dateheure; }
	public void setDateheure(LocalDateTime dateheure) { this.dateheure = dateheure; }
	public Hotel getHotel() { return hotel; }
	public void setHotel(Hotel hotel) { this.hotel = hotel; }
}
