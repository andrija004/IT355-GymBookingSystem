package com.metropolitan.gymbooking.service;

import com.metropolitan.gymbooking.dto.RezervacijaResponse;
import com.metropolitan.gymbooking.dto.TerminResponse;
import com.metropolitan.gymbooking.dto.TrenerDto;
import com.metropolitan.gymbooking.dto.TreningDto;
import com.metropolitan.gymbooking.entity.*;
import com.metropolitan.gymbooking.exception.BadRequestException;
import com.metropolitan.gymbooking.exception.ResourceNotFoundException;
import com.metropolitan.gymbooking.repository.KorisnikRepository;
import com.metropolitan.gymbooking.repository.RezervacijaRepository;
import com.metropolitan.gymbooking.repository.TerminRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Posebna funkcionalnost projekta: lista čekanja (waitlist).
 * Kada je termin popunjen, nova rezervacija se ne odbija nego se stavlja
 * u status NA_CEKANJU. Kada se oslobodi mesto (otkazivanje potvrđene
 * rezervacije), prva sledeća rezervacija na listi čekanja se automatski
 * promoviše u POTVRDJENA.
 */
@Service
public class RezervacijaService {

    private final RezervacijaRepository rezervacijaRepository;
    private final TerminRepository terminRepository;
    private final KorisnikRepository korisnikRepository;

    public RezervacijaService(RezervacijaRepository rezervacijaRepository, TerminRepository terminRepository,
                               KorisnikRepository korisnikRepository) {
        this.rezervacijaRepository = rezervacijaRepository;
        this.terminRepository = terminRepository;
        this.korisnikRepository = korisnikRepository;
    }

    @Transactional
    public RezervacijaResponse rezervisi(String korisnikEmail, Long terminId) {
        Korisnik korisnik = getKorisnikOrThrow(korisnikEmail);
        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new ResourceNotFoundException("Termin sa id " + terminId + " ne postoji"));

        boolean vecPostoji = rezervacijaRepository.findByKorisnikAndTerminAndStatusNot(
                korisnik, termin, StatusRezervacije.OTKAZANA).isPresent();
        if (vecPostoji) {
            throw new BadRequestException("Već imate aktivnu rezervaciju za ovaj termin");
        }

        long potvrdjenih = rezervacijaRepository.countByTerminIdAndStatus(terminId, StatusRezervacije.POTVRDJENA);
        StatusRezervacije status = potvrdjenih < termin.getKapacitet()
                ? StatusRezervacije.POTVRDJENA
                : StatusRezervacije.NA_CEKANJU;

        Rezervacija rezervacija = new Rezervacija(korisnik, termin, status);
        rezervacija = rezervacijaRepository.save(rezervacija);

        return toResponse(rezervacija);
    }

    @Transactional
    public void otkazi(String korisnikEmail, Long rezervacijaId) {
        Korisnik korisnik = getKorisnikOrThrow(korisnikEmail);
        Rezervacija rezervacija = rezervacijaRepository.findById(rezervacijaId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezervacija sa id " + rezervacijaId + " ne postoji"));

        boolean jeVlasnik = rezervacija.getKorisnik().getId().equals(korisnik.getId());
        boolean jeAdmin = korisnik.getUloga() == Uloga.ADMIN;
        if (!jeVlasnik && !jeAdmin) {
            throw new AccessDeniedException("Ne možete otkazati tuđu rezervaciju");
        }

        if (rezervacija.getStatus() == StatusRezervacije.OTKAZANA) {
            throw new BadRequestException("Rezervacija je već otkazana");
        }

        boolean osloboditiMesto = rezervacija.getStatus() == StatusRezervacije.POTVRDJENA;
        rezervacija.setStatus(StatusRezervacije.OTKAZANA);

        if (osloboditiMesto) {
            promovisiSaListeCekanja(rezervacija.getTermin().getId());
        }
    }

    private void promovisiSaListeCekanja(Long terminId) {
        List<Rezervacija> listaCekanja = rezervacijaRepository
                .findByTerminIdAndStatusOrderByDatumRezervacijeAsc(terminId, StatusRezervacije.NA_CEKANJU);

        if (!listaCekanja.isEmpty()) {
            Rezervacija sledeci = listaCekanja.get(0);
            sledeci.setStatus(StatusRezervacije.POTVRDJENA);
        }
    }

    public List<RezervacijaResponse> mojeRezervacije(String korisnikEmail) {
        Korisnik korisnik = getKorisnikOrThrow(korisnikEmail);
        return rezervacijaRepository.findByKorisnikId(korisnik.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<RezervacijaResponse> sveRezervacije() {
        return rezervacijaRepository.findAll().stream().map(this::toResponse).toList();
    }

    private Korisnik getKorisnikOrThrow(String email) {
        return korisnikRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik sa email adresom " + email + " ne postoji"));
    }

    private RezervacijaResponse toResponse(Rezervacija rezervacija) {
        Termin termin = rezervacija.getTermin();
        long potvrdjenih = rezervacijaRepository.countByTerminIdAndStatus(termin.getId(), StatusRezervacije.POTVRDJENA);

        TreningDto treningDto = new TreningDto(termin.getTrening().getId(), termin.getTrening().getNaziv(),
                termin.getTrening().getOpis(), termin.getTrening().getTrajanjeMinuta());
        TrenerDto trenerDto = new TrenerDto(termin.getTrener().getId(), termin.getTrener().getIme(),
                termin.getTrener().getPrezime(), termin.getTrener().getSpecijalnost(), termin.getTrener().getBiografija());
        TerminResponse terminResponse = new TerminResponse(termin.getId(), termin.getDatumVreme(),
                termin.getKapacitet(), potvrdjenih, treningDto, trenerDto);

        return new RezervacijaResponse(rezervacija.getId(), rezervacija.getStatus(),
                rezervacija.getDatumRezervacije(), terminResponse, rezervacija.getKorisnik().getEmail());
    }
}
