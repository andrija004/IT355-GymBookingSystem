package com.metropolitan.gymbooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "korisnici", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Korisnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ime;

    @Column(nullable = false)
    private String prezime;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String lozinka;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Uloga uloga = Uloga.KORISNIK;

    @Column(nullable = false)
    private LocalDateTime datumRegistracije = LocalDateTime.now();

    @OneToMany(mappedBy = "korisnik", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rezervacija> rezervacije = new ArrayList<>();

    public Korisnik() {
    }

    public Korisnik(String ime, String prezime, String email, String lozinka, Uloga uloga) {
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.lozinka = lozinka;
        this.uloga = uloga;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public Uloga getUloga() {
        return uloga;
    }

    public void setUloga(Uloga uloga) {
        this.uloga = uloga;
    }

    public LocalDateTime getDatumRegistracije() {
        return datumRegistracije;
    }

    public void setDatumRegistracije(LocalDateTime datumRegistracije) {
        this.datumRegistracije = datumRegistracije;
    }

    public List<Rezervacija> getRezervacije() {
        return rezervacije;
    }

    public void setRezervacije(List<Rezervacija> rezervacije) {
        this.rezervacije = rezervacije;
    }
}
