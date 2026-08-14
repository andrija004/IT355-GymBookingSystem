package com.metropolitan.gymbooking.service;

import com.metropolitan.gymbooking.dto.TreningDto;
import com.metropolitan.gymbooking.entity.Trening;
import com.metropolitan.gymbooking.exception.BadRequestException;
import com.metropolitan.gymbooking.exception.ResourceNotFoundException;
import com.metropolitan.gymbooking.repository.TerminRepository;
import com.metropolitan.gymbooking.repository.TreningRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TreningService {

    private final TreningRepository treningRepository;
    private final TerminRepository terminRepository;

    public TreningService(TreningRepository treningRepository, TerminRepository terminRepository) {
        this.treningRepository = treningRepository;
        this.terminRepository = terminRepository;
    }

    public List<TreningDto> findAll() {
        return treningRepository.findAll().stream().map(this::toDto).toList();
    }

    public TreningDto findById(Long id) {
        return toDto(getTreningOrThrow(id));
    }

    @Transactional
    public TreningDto create(TreningDto dto) {
        Trening trening = new Trening(dto.getNaziv(), dto.getOpis(), dto.getTrajanjeMinuta());
        return toDto(treningRepository.save(trening));
    }

    @Transactional
    public TreningDto update(Long id, TreningDto dto) {
        Trening trening = getTreningOrThrow(id);
        trening.setNaziv(dto.getNaziv());
        trening.setOpis(dto.getOpis());
        trening.setTrajanjeMinuta(dto.getTrajanjeMinuta());
        return toDto(trening);
    }

    @Transactional
    public void delete(Long id) {
        Trening trening = getTreningOrThrow(id);
        if (terminRepository.existsByTreningId(id)) {
            throw new BadRequestException(
                    "Trening se ne može obrisati jer ima zakazane termine. Prvo uklonite ili preraspodelite termine.");
        }
        treningRepository.delete(trening);
    }

    Trening getTreningOrThrow(Long id) {
        return treningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trening sa id " + id + " ne postoji"));
    }

    private TreningDto toDto(Trening trening) {
        return new TreningDto(trening.getId(), trening.getNaziv(), trening.getOpis(), trening.getTrajanjeMinuta());
    }
}
