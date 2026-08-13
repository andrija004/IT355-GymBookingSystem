package com.metropolitan.gymbooking.dto;

import java.time.LocalDateTime;

public class TerminResponse {

    private Long id;
    private LocalDateTime datumVreme;
    private Integer kapacitet;
    private long brojPotvrdjenih;
    private long slobodnaMesta;
    private TreningDto trening;
    private TrenerDto trener;

    public TerminResponse(Long id, LocalDateTime datumVreme, Integer kapacitet, long brojPotvrdjenih,
                           TreningDto trening, TrenerDto trener) {
        this.id = id;
        this.datumVreme = datumVreme;
        this.kapacitet = kapacitet;
        this.brojPotvrdjenih = brojPotvrdjenih;
        this.slobodnaMesta = Math.max(0, kapacitet - brojPotvrdjenih);
        this.trening = trening;
        this.trener = trener;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDatumVreme() {
        return datumVreme;
    }

    public Integer getKapacitet() {
        return kapacitet;
    }

    public long getBrojPotvrdjenih() {
        return brojPotvrdjenih;
    }

    public long getSlobodnaMesta() {
        return slobodnaMesta;
    }

    public TreningDto getTrening() {
        return trening;
    }

    public TrenerDto getTrener() {
        return trener;
    }
}
