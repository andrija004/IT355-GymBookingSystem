package com.metropolitan.gymbooking.service;

import com.metropolitan.gymbooking.dto.RezervacijaResponse;
import com.metropolitan.gymbooking.entity.*;
import com.metropolitan.gymbooking.exception.BadRequestException;
import com.metropolitan.gymbooking.repository.KorisnikRepository;
import com.metropolitan.gymbooking.repository.RezervacijaRepository;
import com.metropolitan.gymbooking.repository.TerminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Jedinični testovi za servisni sloj rezervacija, sa fokusom na
 * posebnu funkcionalnost projekta: listu čekanja (waitlist).
 */
@ExtendWith(MockitoExtension.class)
class RezervacijaServiceTest {

    @Mock
    private RezervacijaRepository rezervacijaRepository;

    @Mock
    private TerminRepository terminRepository;

    @Mock
    private KorisnikRepository korisnikRepository;

    @InjectMocks
    private RezervacijaService rezervacijaService;

    private Korisnik korisnik;
    private Trener trener;
    private Trening trening;
    private Termin termin;

    @BeforeEach
    void setUp() {
        korisnik = new Korisnik("Marko", "Marković", "marko@primer.rs", "hash", Uloga.KORISNIK);
        setId(korisnik, 1L);

        trener = new Trener("Nikola", "Nikolić", "Kondicioni trening", "Bio");
        setId(trener, 1L);

        trening = new Trening("Crossfit", "Opis", 60);
        setId(trening, 1L);

        termin = new Termin(LocalDateTime.now().plusDays(1), 2, trening, trener);
        setId(termin, 1L);
    }

    @Test
    void rezervisi_kadaImaSlobodnihMesta_kreiraPotvrdjenuRezervaciju() {
        given(korisnikRepository.findByEmail("marko@primer.rs")).willReturn(Optional.of(korisnik));
        given(terminRepository.findById(1L)).willReturn(Optional.of(termin));
        given(rezervacijaRepository.findByKorisnikAndTerminAndStatusNot(any(), any(), any()))
                .willReturn(Optional.empty());
        given(rezervacijaRepository.countByTerminIdAndStatus(1L, StatusRezervacije.POTVRDJENA)).willReturn(0L);
        given(rezervacijaRepository.save(any(Rezervacija.class))).willAnswer(invocation -> {
            Rezervacija r = invocation.getArgument(0);
            setId(r, 100L);
            return r;
        });

        RezervacijaResponse response = rezervacijaService.rezervisi("marko@primer.rs", 1L);

        assertThat(response.getStatus()).isEqualTo(StatusRezervacije.POTVRDJENA);
        verify(rezervacijaRepository).save(any(Rezervacija.class));
    }

    @Test
    void rezervisi_kadaJeTerminPopunjen_stavljaRezervacijuNaListuCekanja() {
        given(korisnikRepository.findByEmail("marko@primer.rs")).willReturn(Optional.of(korisnik));
        given(terminRepository.findById(1L)).willReturn(Optional.of(termin));
        given(rezervacijaRepository.findByKorisnikAndTerminAndStatusNot(any(), any(), any()))
                .willReturn(Optional.empty());
        // Termin ima kapacitet 2, već ima 2 potvrđene rezervacije -> pun je
        given(rezervacijaRepository.countByTerminIdAndStatus(1L, StatusRezervacije.POTVRDJENA)).willReturn(2L);
        given(rezervacijaRepository.save(any(Rezervacija.class))).willAnswer(invocation -> {
            Rezervacija r = invocation.getArgument(0);
            setId(r, 101L);
            return r;
        });

        RezervacijaResponse response = rezervacijaService.rezervisi("marko@primer.rs", 1L);

        assertThat(response.getStatus()).isEqualTo(StatusRezervacije.NA_CEKANJU);
    }

    @Test
    void rezervisi_kadaVecPostojiAktivnaRezervacija_bacaBadRequestException() {
        Rezervacija postojeca = new Rezervacija(korisnik, termin, StatusRezervacije.POTVRDJENA);

        given(korisnikRepository.findByEmail("marko@primer.rs")).willReturn(Optional.of(korisnik));
        given(terminRepository.findById(1L)).willReturn(Optional.of(termin));
        given(rezervacijaRepository.findByKorisnikAndTerminAndStatusNot(any(), any(), any()))
                .willReturn(Optional.of(postojeca));

        assertThatThrownBy(() -> rezervacijaService.rezervisi("marko@primer.rs", 1L))
                .isInstanceOf(BadRequestException.class);

        verify(rezervacijaRepository, never()).save(any());
    }

    @Test
    void otkazi_potvrdjenuRezervaciju_promovisePrvogSaListeCekanja() {
        Rezervacija potvrdjena = new Rezervacija(korisnik, termin, StatusRezervacije.POTVRDJENA);
        setId(potvrdjena, 5L);

        Korisnik korisnikNaCekanju = new Korisnik("Ana", "Anić", "ana@primer.rs", "hash", Uloga.KORISNIK);
        setId(korisnikNaCekanju, 2L);
        Rezervacija naCekanju = new Rezervacija(korisnikNaCekanju, termin, StatusRezervacije.NA_CEKANJU);
        setId(naCekanju, 6L);

        given(korisnikRepository.findByEmail("marko@primer.rs")).willReturn(Optional.of(korisnik));
        given(rezervacijaRepository.findById(5L)).willReturn(Optional.of(potvrdjena));
        given(rezervacijaRepository.findByTerminIdAndStatusOrderByDatumRezervacijeAsc(1L, StatusRezervacije.NA_CEKANJU))
                .willReturn(List.of(naCekanju));

        rezervacijaService.otkazi("marko@primer.rs", 5L);

        assertThat(potvrdjena.getStatus()).isEqualTo(StatusRezervacije.OTKAZANA);
        assertThat(naCekanju.getStatus()).isEqualTo(StatusRezervacije.POTVRDJENA);
    }

    @Test
    void otkazi_kadaNemaNikogaNaListiCekanja_samoOtkazujeBezPromocije() {
        Rezervacija potvrdjena = new Rezervacija(korisnik, termin, StatusRezervacije.POTVRDJENA);
        setId(potvrdjena, 5L);

        given(korisnikRepository.findByEmail("marko@primer.rs")).willReturn(Optional.of(korisnik));
        given(rezervacijaRepository.findById(5L)).willReturn(Optional.of(potvrdjena));
        given(rezervacijaRepository.findByTerminIdAndStatusOrderByDatumRezervacijeAsc(1L, StatusRezervacije.NA_CEKANJU))
                .willReturn(List.of());

        rezervacijaService.otkazi("marko@primer.rs", 5L);

        assertThat(potvrdjena.getStatus()).isEqualTo(StatusRezervacije.OTKAZANA);
    }

    @Test
    void otkazi_tudjuRezervaciju_bacaAccessDeniedException() {
        Korisnik drugiKorisnik = new Korisnik("Ana", "Anić", "ana@primer.rs", "hash", Uloga.KORISNIK);
        setId(drugiKorisnik, 2L);
        Rezervacija tudja = new Rezervacija(drugiKorisnik, termin, StatusRezervacije.POTVRDJENA);
        setId(tudja, 7L);

        given(korisnikRepository.findByEmail("marko@primer.rs")).willReturn(Optional.of(korisnik));
        given(rezervacijaRepository.findById(7L)).willReturn(Optional.of(tudja));

        assertThatThrownBy(() -> rezervacijaService.otkazi("marko@primer.rs", 7L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void otkazi_vecOtkazanuRezervaciju_bacaBadRequestException() {
        Rezervacija otkazana = new Rezervacija(korisnik, termin, StatusRezervacije.OTKAZANA);
        setId(otkazana, 8L);

        given(korisnikRepository.findByEmail("marko@primer.rs")).willReturn(Optional.of(korisnik));
        given(rezervacijaRepository.findById(8L)).willReturn(Optional.of(otkazana));

        assertThatThrownBy(() -> rezervacijaService.otkazi("marko@primer.rs", 8L))
                .isInstanceOf(BadRequestException.class);
    }

    private static void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
