package com.metropolitan.gymbooking.controller;

import com.metropolitan.gymbooking.dto.RezervacijaRequest;
import com.metropolitan.gymbooking.dto.RezervacijaResponse;
import com.metropolitan.gymbooking.security.KorisnikPrincipal;
import com.metropolitan.gymbooking.service.RezervacijaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rezervacije")
public class RezervacijaController {

    private final RezervacijaService rezervacijaService;

    public RezervacijaController(RezervacijaService rezervacijaService) {
        this.rezervacijaService = rezervacijaService;
    }

    @PostMapping
    public ResponseEntity<RezervacijaResponse> rezervisi(@AuthenticationPrincipal KorisnikPrincipal principal,
                                                           @Valid @RequestBody RezervacijaRequest request) {
        return ResponseEntity.ok(rezervacijaService.rezervisi(principal.getUsername(), request.getTerminId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> otkazi(@AuthenticationPrincipal KorisnikPrincipal principal,
                                        @PathVariable Long id) {
        rezervacijaService.otkazi(principal.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/moje")
    public ResponseEntity<List<RezervacijaResponse>> mojeRezervacije(@AuthenticationPrincipal KorisnikPrincipal principal) {
        return ResponseEntity.ok(rezervacijaService.mojeRezervacije(principal.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RezervacijaResponse>> sveRezervacije() {
        return ResponseEntity.ok(rezervacijaService.sveRezervacije());
    }
}
