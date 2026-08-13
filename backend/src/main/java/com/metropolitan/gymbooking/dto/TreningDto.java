package com.metropolitan.gymbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TreningDto {

    private Long id;

    @NotBlank(message = "Naziv je obavezan")
    private String naziv;

    private String opis;

    @NotNull(message = "Trajanje je obavezno")
    @Positive(message = "Trajanje mora biti pozitivan broj")
    private Integer trajanjeMinuta;

    public TreningDto() {
    }

    public TreningDto(Long id, String naziv, String opis, Integer trajanjeMinuta) {
        this.id = id;
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
}
