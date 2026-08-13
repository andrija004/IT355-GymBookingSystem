package com.metropolitan.gymbooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "termini")
public class Termin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime datumVreme;

    @Column(nullable = false)
    private Integer kapacitet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trening_id", nullable = false)
    private Trening trening;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trener_id", nullable = false)
    private Trener trener;

    @OneToMany(mappedBy = "termin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rezervacija> rezervacije = new ArrayList<>();

    public Termin() {
    }

    public Termin(LocalDateTime datumVreme, Integer kapacitet, Trening trening, Trener trener) {
        this.datumVreme = datumVreme;
        this.kapacitet = kapacitet;
        this.trening = trening;
        this.trener = trener;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDatumVreme() {
        return datumVreme;
    }

    public void setDatumVreme(LocalDateTime datumVreme) {
        this.datumVreme = datumVreme;
    }

    public Integer getKapacitet() {
        return kapacitet;
    }

    public void setKapacitet(Integer kapacitet) {
        this.kapacitet = kapacitet;
    }

    public Trening getTrening() {
        return trening;
    }

    public void setTrening(Trening trening) {
        this.trening = trening;
    }

    public Trener getTrener() {
        return trener;
    }

    public void setTrener(Trener trener) {
        this.trener = trener;
    }

    public List<Rezervacija> getRezervacije() {
        return rezervacije;
    }

    public void setRezervacije(List<Rezervacija> rezervacije) {
        this.rezervacije = rezervacije;
    }
}
