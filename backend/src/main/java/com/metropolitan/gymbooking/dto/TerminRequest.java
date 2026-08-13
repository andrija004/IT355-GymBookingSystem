package com.metropolitan.gymbooking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class TerminRequest {

    @NotNull(message = "Datum i vreme su obavezni")
    private LocalDateTime datumVreme;

    @NotNull(message = "Kapacitet je obavezan")
    @Positive(message = "Kapacitet mora biti pozitivan broj")
    private Integer kapacitet;

    @NotNull(message = "Trening je obavezan")
    private Long treningId;

    @NotNull(message = "Trener je obavezan")
    private Long trenerId;

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

    public Long getTreningId() {
        return treningId;
    }

    public void setTreningId(Long treningId) {
        this.treningId = treningId;
    }

    public Long getTrenerId() {
        return trenerId;
    }

    public void setTrenerId(Long trenerId) {
        this.trenerId = trenerId;
    }
}
