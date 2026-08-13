package com.metropolitan.gymbooking.repository;

import com.metropolitan.gymbooking.entity.Korisnik;
import com.metropolitan.gymbooking.entity.Rezervacija;
import com.metropolitan.gymbooking.entity.StatusRezervacije;
import com.metropolitan.gymbooking.entity.Termin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RezervacijaRepository extends JpaRepository<Rezervacija, Long> {

    List<Rezervacija> findByKorisnikId(Long korisnikId);

    List<Rezervacija> findByTerminIdAndStatus(Long terminId, StatusRezervacije status);

    long countByTerminIdAndStatus(Long terminId, StatusRezervacije status);

    Optional<Rezervacija> findByKorisnikAndTerminAndStatusNot(Korisnik korisnik, Termin termin, StatusRezervacije status);

    List<Rezervacija> findByTerminIdAndStatusOrderByDatumRezervacijeAsc(Long terminId, StatusRezervacije status);
}
