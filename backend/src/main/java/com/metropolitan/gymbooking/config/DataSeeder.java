package com.metropolitan.gymbooking.config;

import com.metropolitan.gymbooking.entity.*;
import com.metropolitan.gymbooking.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Puni bazu početnim podacima (admin nalog + primeri trenera/treninga/termina)
 * radi lakšeg testiranja i demonstracije aplikacije.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final KorisnikRepository korisnikRepository;
    private final TrenerRepository trenerRepository;
    private final TreningRepository treningRepository;
    private final TerminRepository terminRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(KorisnikRepository korisnikRepository, TrenerRepository trenerRepository,
                       TreningRepository treningRepository, TerminRepository terminRepository,
                       PasswordEncoder passwordEncoder) {
        this.korisnikRepository = korisnikRepository;
        this.trenerRepository = trenerRepository;
        this.treningRepository = treningRepository;
        this.terminRepository = terminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (korisnikRepository.count() > 0) {
            return;
        }

        Korisnik admin = new Korisnik("Admin", "Administratorović", "admin@teretana.rs",
                passwordEncoder.encode("admin123"), Uloga.ADMIN);
        Korisnik korisnik = new Korisnik("Marko", "Marković", "marko@primer.rs",
                passwordEncoder.encode("marko123"), Uloga.KORISNIK);
        korisnikRepository.save(admin);
        korisnikRepository.save(korisnik);

        Trener trener1 = trenerRepository.save(new Trener("Nikola", "Nikolić", "Kondiciona priprema",
                "Sertifikovani fitnes trener sa 8 godina iskustva."));
        Trener trener2 = trenerRepository.save(new Trener("Jovana", "Jovanović", "Joga i pilates",
                "Instruktor joge i pilatesa, fokus na fleksibilnost i mentalno zdravlje."));

        Trening trening1 = treningRepository.save(new Trening("Crossfit", "Intenzivan grupni trening funkcionalnih vežbi.", 60));
        Trening trening2 = treningRepository.save(new Trening("Joga", "Opuštajući trening fleksibilnosti i disanja.", 45));

        LocalDateTime sada = LocalDateTime.now();
        terminRepository.save(new Termin(sada.plusDays(1).withHour(9).withMinute(0), 2, trening1, trener1));
        terminRepository.save(new Termin(sada.plusDays(1).withHour(18).withMinute(0), 10, trening2, trener2));
        terminRepository.save(new Termin(sada.plusDays(2).withHour(10).withMinute(0), 8, trening1, trener1));
    }
}
