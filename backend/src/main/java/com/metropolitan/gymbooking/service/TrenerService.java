package com.metropolitan.gymbooking.service;

import com.metropolitan.gymbooking.dto.TrenerDto;
import com.metropolitan.gymbooking.entity.Trener;
import com.metropolitan.gymbooking.exception.BadRequestException;
import com.metropolitan.gymbooking.exception.ResourceNotFoundException;
import com.metropolitan.gymbooking.repository.TerminRepository;
import com.metropolitan.gymbooking.repository.TrenerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrenerService {

    private final TrenerRepository trenerRepository;
    private final TerminRepository terminRepository;

    public TrenerService(TrenerRepository trenerRepository, TerminRepository terminRepository) {
        this.trenerRepository = trenerRepository;
        this.terminRepository = terminRepository;
    }

    public List<TrenerDto> findAll() {
        return trenerRepository.findAll().stream().map(this::toDto).toList();
    }

    public TrenerDto findById(Long id) {
        return toDto(getTrenerOrThrow(id));
    }

    @Transactional
    public TrenerDto create(TrenerDto dto) {
        Trener trener = new Trener(dto.getIme(), dto.getPrezime(), dto.getSpecijalnost(), dto.getBiografija());
        return toDto(trenerRepository.save(trener));
    }

    @Transactional
    public TrenerDto update(Long id, TrenerDto dto) {
        Trener trener = getTrenerOrThrow(id);
        trener.setIme(dto.getIme());
        trener.setPrezime(dto.getPrezime());
        trener.setSpecijalnost(dto.getSpecijalnost());
        trener.setBiografija(dto.getBiografija());
        return toDto(trener);
    }

    @Transactional
    public void delete(Long id) {
        Trener trener = getTrenerOrThrow(id);
        if (terminRepository.existsByTrenerId(id)) {
            throw new BadRequestException(
                    "Trener se ne može obrisati jer ima zakazane termine. Prvo uklonite ili preraspodelite termine.");
        }
        trenerRepository.delete(trener);
    }

    private Trener getTrenerOrThrow(Long id) {
        return trenerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trener sa id " + id + " ne postoji"));
    }

    private TrenerDto toDto(Trener trener) {
        return new TrenerDto(trener.getId(), trener.getIme(), trener.getPrezime(),
                trener.getSpecijalnost(), trener.getBiografija());
    }
}
