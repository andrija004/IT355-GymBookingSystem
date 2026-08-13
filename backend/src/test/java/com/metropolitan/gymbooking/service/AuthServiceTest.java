package com.metropolitan.gymbooking.service;

import com.metropolitan.gymbooking.dto.AuthResponse;
import com.metropolitan.gymbooking.dto.RegisterRequest;
import com.metropolitan.gymbooking.entity.Korisnik;
import com.metropolitan.gymbooking.entity.Uloga;
import com.metropolitan.gymbooking.exception.BadRequestException;
import com.metropolitan.gymbooking.repository.KorisnikRepository;
import com.metropolitan.gymbooking.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private KorisnikRepository korisnikRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_saValidnimPodacima_kreiraKorisnikaIVracaToken() {
        RegisterRequest request = new RegisterRequest();
        request.setIme("Marko");
        request.setPrezime("Marković");
        request.setEmail("marko@primer.rs");
        request.setLozinka("lozinka123");

        given(korisnikRepository.existsByEmail("marko@primer.rs")).willReturn(false);
        given(passwordEncoder.encode("lozinka123")).willReturn("hashovana-lozinka");
        given(jwtService.generateToken(any())).willReturn("fake-jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getEmail()).isEqualTo("marko@primer.rs");
        assertThat(response.getUloga()).isEqualTo(Uloga.KORISNIK.name());
        verify(korisnikRepository).save(any(Korisnik.class));
    }

    @Test
    void register_kadaEmailVecPostoji_bacaBadRequestException() {
        RegisterRequest request = new RegisterRequest();
        request.setIme("Marko");
        request.setPrezime("Marković");
        request.setEmail("marko@primer.rs");
        request.setLozinka("lozinka123");

        given(korisnikRepository.existsByEmail("marko@primer.rs")).willReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("već postoji");

        verify(korisnikRepository, never()).save(any());
    }
}
