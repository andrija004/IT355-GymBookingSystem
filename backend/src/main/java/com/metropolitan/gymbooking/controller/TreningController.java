package com.metropolitan.gymbooking.controller;

import com.metropolitan.gymbooking.dto.TreningDto;
import com.metropolitan.gymbooking.service.TreningService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treninzi")
public class TreningController {

    private final TreningService treningService;

    public TreningController(TreningService treningService) {
        this.treningService = treningService;
    }

    @GetMapping
    public ResponseEntity<List<TreningDto>> findAll() {
        return ResponseEntity.ok(treningService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreningDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(treningService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TreningDto> create(@Valid @RequestBody TreningDto dto) {
        return ResponseEntity.ok(treningService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreningDto> update(@PathVariable Long id, @Valid @RequestBody TreningDto dto) {
        return ResponseEntity.ok(treningService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        treningService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
