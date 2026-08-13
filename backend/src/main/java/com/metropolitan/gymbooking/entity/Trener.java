package com.metropolitan.gymbooking.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "treneri")
public class Trener {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ime;

    @Column(nullable = false)
    private String prezime;

    @Column(nullable = false)
    private String specijalnost;

    @Column(length = 1000)
    private String biografija;

    @OneToMany(mappedBy = "trener", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Termin> termini = new ArrayList<>();

    public Trener() {
    }

    public Trener(String ime, String prezime, String specijalnost, String biografija) {
        this.ime = ime;
        this.prezime = prezime;
        this.specijalnost = specijalnost;
        this.biografija = biografija;
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

    public String getSpecijalnost() {
        return specijalnost;
    }

    public void setSpecijalnost(String specijalnost) {
        this.specijalnost = specijalnost;
    }

    public String getBiografija() {
        return biografija;
    }

    public void setBiografija(String biografija) {
        this.biografija = biografija;
    }

    public List<Termin> getTermini() {
        return termini;
    }

    public void setTermini(List<Termin> termini) {
        this.termini = termini;
    }
}
