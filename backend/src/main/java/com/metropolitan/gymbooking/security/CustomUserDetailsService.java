package com.metropolitan.gymbooking.security;

import com.metropolitan.gymbooking.entity.Korisnik;
import com.metropolitan.gymbooking.repository.KorisnikRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final KorisnikRepository korisnikRepository;

    public CustomUserDetailsService(KorisnikRepository korisnikRepository) {
        this.korisnikRepository = korisnikRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Korisnik korisnik = korisnikRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik sa email adresom '" + email + "' ne postoji"));
        return new KorisnikPrincipal(korisnik);
    }
}
