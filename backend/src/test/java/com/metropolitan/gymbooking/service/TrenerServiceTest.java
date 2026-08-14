package com.metropolitan.gymbooking.service;

import com.metropolitan.gymbooking.dto.TrenerDto;
import com.metropolitan.gymbooking.entity.Trener;
import com.metropolitan.gymbooking.exception.BadRequestException;
import com.metropolitan.gymbooking.exception.ResourceNotFoundException;
import com.metropolitan.gymbooking.repository.TerminRepository;
import com.metropolitan.gymbooking.repository.TrenerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrenerServiceTest {

    @Mock
    private TrenerRepository trenerRepository;

    @Mock
    private TerminRepository terminRepository;

    @InjectMocks
    private TrenerService trenerService;

    @Test
    void create_kreiraNovogTrenera() {
        TrenerDto dto = new TrenerDto(null, "Nikola", "Nikolić", "Kondiciona priprema", "Bio");
        given(trenerRepository.save(any(Trener.class))).willAnswer(invocation -> {
            Trener t = invocation.getArgument(0);
            setId(t, 1L);
            return t;
        });

        TrenerDto result = trenerService.create(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getIme()).isEqualTo("Nikola");
        verify(trenerRepository).save(any(Trener.class));
    }

    @Test
    void findById_kadaTrenerNePostoji_bacaResourceNotFoundException() {
        given(trenerRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> trenerService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_postojeciTrenerBezTermina_brisePrekoRepozitorijuma() {
        Trener trener = new Trener("Nikola", "Nikolić", "Kondicija", "Bio");
        setId(trener, 3L);
        given(trenerRepository.findById(3L)).willReturn(Optional.of(trener));
        given(terminRepository.existsByTrenerId(3L)).willReturn(false);

        trenerService.delete(3L);

        verify(trenerRepository).delete(trener);
    }

    @Test
    void delete_trenerImaZakazaneTermine_bacaBadRequestException() {
        Trener trener = new Trener("Nikola", "Nikolić", "Kondicija", "Bio");
        setId(trener, 4L);
        given(trenerRepository.findById(4L)).willReturn(Optional.of(trener));
        given(terminRepository.existsByTrenerId(4L)).willReturn(true);

        assertThatThrownBy(() -> trenerService.delete(4L))
                .isInstanceOf(BadRequestException.class);

        verify(trenerRepository, never()).delete(any());
    }

    private static void setId(Trener trener, Long id) {
        try {
            var field = Trener.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(trener, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
