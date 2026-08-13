package com.metropolitan.gymbooking.controller;

import com.metropolitan.gymbooking.dto.TerminRequest;
import com.metropolitan.gymbooking.dto.TerminResponse;
import com.metropolitan.gymbooking.service.TerminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/termini")
public class TerminController {

    private final TerminService terminService;

    public TerminController(TerminService terminService) {
        this.terminService = terminService;
    }

    @GetMapping
    public ResponseEntity<List<TerminResponse>> findAllBuduce() {
        return ResponseEntity.ok(terminService.findAllBuduce());
    }

    @GetMapping("/svi")
    public ResponseEntity<List<TerminResponse>> findAll() {
        return ResponseEntity.ok(terminService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TerminResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(terminService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TerminResponse> create(@Valid @RequestBody TerminRequest request) {
        return ResponseEntity.ok(terminService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TerminResponse> update(@PathVariable Long id, @Valid @RequestBody TerminRequest request) {
        return ResponseEntity.ok(terminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        terminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
