package com.metropolitan.gymbooking.dto;

public class AuthResponse {

    private String token;
    private String email;
    private String ime;
    private String prezime;
    private String uloga;

    public AuthResponse(String token, String email, String ime, String prezime, String uloga) {
        this.token = token;
        this.email = email;
        this.ime = ime;
        this.prezime = prezime;
        this.uloga = uloga;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getUloga() {
        return uloga;
    }
}
