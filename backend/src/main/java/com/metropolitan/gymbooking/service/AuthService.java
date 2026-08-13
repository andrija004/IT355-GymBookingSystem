package com.metropolitan.gymbooking.service;

import com.metropolitan.gymbooking.dto.AuthResponse;
import com.metropolitan.gymbooking.dto.LoginRequest;
import com.metropolitan.gymbooking.dto.RegisterRequest;
import com.metropolitan.gymbooking.entity.Korisnik;
import com.metropolitan.gymbooking.entity.Uloga;
import com.metropolitan.gymbooking.exception.BadRequestException;
import com.metropolitan.gymbooking.repository.KorisnikRepository;
import com.metropolitan.gymbooking.security.JwtService;
import com.metropolitan.gymbooking.security.KorisnikPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final KorisnikRepository korisnikRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(KorisnikRepository korisnikRepository, PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager, JwtService jwtService) {
        this.korisnikRepository = korisnikRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (korisnikRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Korisnik sa ovim email-om već postoji");
        }

        Korisnik korisnik = new Korisnik(
                request.getIme(),
                request.getPrezime(),
                request.getEmail(),
                passwordEncoder.encode(request.getLozinka()),
                Uloga.KORISNIK
        );
        korisnikRepository.save(korisnik);

        KorisnikPrincipal principal = new KorisnikPrincipal(korisnik);
        String token = jwtService.generateToken(principal);

        return new AuthResponse(token, korisnik.getEmail(), korisnik.getIme(), korisnik.getPrezime(),
                korisnik.getUloga().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getLozinka()));

        Korisnik korisnik = korisnikRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Pogrešan email ili lozinka"));

        KorisnikPrincipal principal = new KorisnikPrincipal(korisnik);
        String token = jwtService.generateToken(principal);

        return new AuthResponse(token, korisnik.getEmail(), korisnik.getIme(), korisnik.getPrezime(),
                korisnik.getUloga().name());
    }
}
