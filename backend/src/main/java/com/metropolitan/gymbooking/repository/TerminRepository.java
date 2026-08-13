package com.metropolitan.gymbooking.repository;

import com.metropolitan.gymbooking.entity.Termin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TerminRepository extends JpaRepository<Termin, Long> {

    List<Termin> findByDatumVremeAfterOrderByDatumVremeAsc(LocalDateTime from);

    List<Termin> findAllByOrderByDatumVremeAsc();
}
