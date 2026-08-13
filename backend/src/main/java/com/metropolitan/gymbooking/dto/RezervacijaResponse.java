package com.metropolitan.gymbooking.dto;

import com.metropolitan.gymbooking.entity.StatusRezervacije;

import java.time.LocalDateTime;

public class RezervacijaResponse {

    private Long id;
    private StatusRezervacije status;
    private LocalDateTime datumRezervacije;
    private TerminResponse termin;
    private String korisnikEmail;

    public RezervacijaResponse(Long id, StatusRezervacije status, LocalDateTime datumRezervacije,
                                TerminResponse termin, String korisnikEmail) {
        this.id = id;
        this.status = status;
        this.datumRezervacije = datumRezervacije;
        this.termin = termin;
        this.korisnikEmail = korisnikEmail;
    }

    public Long getId() {
        return id;
    }

    public StatusRezervacije getStatus() {
        return status;
    }

    public LocalDateTime getDatumRezervacije() {
        return datumRezervacije;
    }

    public TerminResponse getTermin() {
        return termin;
    }

    public String getKorisnikEmail() {
        return korisnikEmail;
    }
}
