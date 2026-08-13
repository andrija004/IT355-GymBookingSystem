package com.metropolitan.gymbooking.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "treninzi")
public class Trening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String naziv;

    @Column(length = 1000)
    private String opis;

    @Column(nullable = false)
    private Integer trajanjeMinuta;

    @OneToMany(mappedBy = "trening", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Termin> termini = new ArrayList<>();

    public Trening() {
    }

    public Trening(String naziv, String opis, Integer trajanjeMinuta) {
        this.naziv = naziv;
        this.opis = opis;
        this.trajanjeMinuta = trajanjeMinuta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Integer getTrajanjeMinuta() {
        return trajanjeMinuta;
    }

    public void setTrajanjeMinuta(Integer trajanjeMinuta) {
        this.trajanjeMinuta = trajanjeMinuta;
    }

    public List<Termin> getTermini() {
        return termini;
    }

    public void setTermini(List<Termin> termini) {
        this.termini = termini;
    }
}
