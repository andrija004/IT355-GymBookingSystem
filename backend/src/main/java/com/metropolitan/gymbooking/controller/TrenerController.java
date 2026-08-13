package com.metropolitan.gymbooking.controller;

import com.metropolitan.gymbooking.dto.TrenerDto;
import com.metropolitan.gymbooking.service.TrenerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treneri")
public class TrenerController {

    private final TrenerService trenerService;

    public TrenerController(TrenerService trenerService) {
        this.trenerService = trenerService;
    }

    @GetMapping
    public ResponseEntity<List<TrenerDto>> findAll() {
        return ResponseEntity.ok(trenerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrenerDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(trenerService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TrenerDto> create(@Valid @RequestBody TrenerDto dto) {
        return ResponseEntity.ok(trenerService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrenerDto> update(@PathVariable Long id, @Valid @RequestBody TrenerDto dto) {
        return ResponseEntity.ok(trenerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        trenerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
