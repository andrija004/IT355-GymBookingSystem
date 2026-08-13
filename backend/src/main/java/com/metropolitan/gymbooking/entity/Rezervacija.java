package com.metropolitan.gymbooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rezervacije", uniqueConstraints = @UniqueConstraint(columnNames = {"korisnik_id", "termin_id"}))
public class Rezervacija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "korisnik_id", nullable = false)
    private Korisnik korisnik;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "termin_id", nullable = false)
    private Termin termin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusRezervacije status = StatusRezervacije.POTVRDJENA;

    @Column(nullable = false)
    private LocalDateTime datumRezervacije = LocalDateTime.now();

    public Rezervacija() {
    }

    public Rezervacija(Korisnik korisnik, Termin termin, StatusRezervacije status) {
        this.korisnik = korisnik;
        this.termin = termin;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
    }

    public Termin getTermin() {
        return termin;
    }

    public void setTermin(Termin termin) {
        this.termin = termin;
    }

    public StatusRezervacije getStatus() {
        return status;
    }

    public void setStatus(StatusRezervacije status) {
        this.status = status;
    }

    public LocalDateTime getDatumRezervacije() {
        return datumRezervacije;
    }

    public void setDatumRezervacije(LocalDateTime datumRezervacije) {
        this.datumRezervacije = datumRezervacije;
    }
}
