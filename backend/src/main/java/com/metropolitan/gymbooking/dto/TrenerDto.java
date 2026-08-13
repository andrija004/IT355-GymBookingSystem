package com.metropolitan.gymbooking.dto;

import jakarta.validation.constraints.NotBlank;

public class TrenerDto {

    private Long id;

    @NotBlank(message = "Ime je obavezno")
    private String ime;

    @NotBlank(message = "Prezime je obavezno")
    private String prezime;

    @NotBlank(message = "Specijalnost je obavezna")
    private String specijalnost;

    private String biografija;

    public TrenerDto() {
    }

    public TrenerDto(Long id, String ime, String prezime, String specijalnost, String biografija) {
        this.id = id;
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
}
