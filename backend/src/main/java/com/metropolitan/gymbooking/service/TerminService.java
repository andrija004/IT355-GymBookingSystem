package com.metropolitan.gymbooking.service;

import com.metropolitan.gymbooking.dto.TerminRequest;
import com.metropolitan.gymbooking.dto.TerminResponse;
import com.metropolitan.gymbooking.dto.TrenerDto;
import com.metropolitan.gymbooking.dto.TreningDto;
import com.metropolitan.gymbooking.entity.StatusRezervacije;
import com.metropolitan.gymbooking.entity.Termin;
import com.metropolitan.gymbooking.entity.Trener;
import com.metropolitan.gymbooking.entity.Trening;
import com.metropolitan.gymbooking.exception.BadRequestException;
import com.metropolitan.gymbooking.exception.ResourceNotFoundException;
import com.metropolitan.gymbooking.repository.RezervacijaRepository;
import com.metropolitan.gymbooking.repository.TerminRepository;
import com.metropolitan.gymbooking.repository.TrenerRepository;
import com.metropolitan.gymbooking.repository.TreningRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TerminService {

    private final TerminRepository terminRepository;
    private final TrenerRepository trenerRepository;
    private final TreningRepository treningRepository;
    private final RezervacijaRepository rezervacijaRepository;

    public TerminService(TerminRepository terminRepository, TrenerRepository trenerRepository,
                          TreningRepository treningRepository, RezervacijaRepository rezervacijaRepository) {
        this.terminRepository = terminRepository;
        this.trenerRepository = trenerRepository;
        this.treningRepository = treningRepository;
        this.rezervacijaRepository = rezervacijaRepository;
    }

    public List<TerminResponse> findAllBuduce() {
        return terminRepository.findByDatumVremeAfterOrderByDatumVremeAsc(LocalDateTime.now())
                .stream().map(this::toResponse).toList();
    }

    public List<TerminResponse> findAll() {
        return terminRepository.findAllByOrderByDatumVremeAsc().stream().map(this::toResponse).toList();
    }

    public TerminResponse findById(Long id) {
        return toResponse(getTerminOrThrow(id));
    }

    @Transactional
    public TerminResponse create(TerminRequest request) {
        Trening trening = treningRepository.findById(request.getTreningId())
                .orElseThrow(() -> new ResourceNotFoundException("Trening sa id " + request.getTreningId() + " ne postoji"));
        Trener trener = trenerRepository.findById(request.getTrenerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trener sa id " + request.getTrenerId() + " ne postoji"));

        if (request.getDatumVreme().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Termin ne može biti zakazan u prošlosti");
        }

        Termin termin = new Termin(request.getDatumVreme(), request.getKapacitet(), trening, trener);
        return toResponse(terminRepository.save(termin));
    }

    @Transactional
    public TerminResponse update(Long id, TerminRequest request) {
        Termin termin = getTerminOrThrow(id);

        Trening trening = treningRepository.findById(request.getTreningId())
                .orElseThrow(() -> new ResourceNotFoundException("Trening sa id " + request.getTreningId() + " ne postoji"));
        Trener trener = trenerRepository.findById(request.getTrenerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trener sa id " + request.getTrenerId() + " ne postoji"));

        long potvrdjenih = rezervacijaRepository.countByTerminIdAndStatus(id, StatusRezervacije.POTVRDJENA);
        if (request.getKapacitet() < potvrdjenih) {
            throw new BadRequestException("Kapacitet ne može biti manji od broja već potvrđenih rezervacija (" + potvrdjenih + ")");
        }

        termin.setDatumVreme(request.getDatumVreme());
        termin.setKapacitet(request.getKapacitet());
        termin.setTrening(trening);
        termin.setTrener(trener);

        return toResponse(termin);
    }

    @Transactional
    public void delete(Long id) {
        Termin termin = getTerminOrThrow(id);
        terminRepository.delete(termin);
    }

    Termin getTerminOrThrow(Long id) {
        return terminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Termin sa id " + id + " ne postoji"));
    }

    private TerminResponse toResponse(Termin termin) {
        long potvrdjenih = rezervacijaRepository.countByTerminIdAndStatus(termin.getId(), StatusRezervacije.POTVRDJENA);
        TreningDto treningDto = new TreningDto(termin.getTrening().getId(), termin.getTrening().getNaziv(),
                termin.getTrening().getOpis(), termin.getTrening().getTrajanjeMinuta());
        TrenerDto trenerDto = new TrenerDto(termin.getTrener().getId(), termin.getTrener().getIme(),
                termin.getTrener().getPrezime(), termin.getTrener().getSpecijalnost(), termin.getTrener().getBiografija());
        return new TerminResponse(termin.getId(), termin.getDatumVreme(), termin.getKapacitet(),
                potvrdjenih, treningDto, trenerDto);
    }
}
