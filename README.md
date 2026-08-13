# Gym Booking System

Full-stack aplikacija za upravljanje rezervacijama termina u teretani — drugi projektni zadatak, IT355 (Veb sistemi 2).

Akteri: **Korisnik** i **Administrator**. Entiteti: `Korisnik`, `Trener`, `Trening`, `Termin`, `Rezervacija`.

Posebna funkcionalnost: **lista čekanja (waitlist)** — kada je termin popunjen, rezervacija dobija status `NA_CEKANJU`; otkazivanjem potvrđene rezervacije prva sledeća sa liste čekanja se automatski promoviše u `POTVRDJENA`.

Puna dokumentacija projekta (use-case, ER dijagram, arhitektura, bezbednost, testiranje): [`docs/IT355-PZ02-Dokumentacija-AndrijaMilenkovic.docx`](docs/IT355-PZ02-Dokumentacija-AndrijaMilenkovic.docx).

## Tehnologije

- **Backend**: Java 17, Spring Boot 3.3, Spring Web, Spring Data JPA, Spring Security + JWT, H2
- **Testiranje**: JUnit 5, Mockito, Spring Boot Test, MockMvc
- **Frontend**: React 19 (Vite), React Router, Axios

## Pokretanje backend-a

Zahteva JDK 17+ i Maven (ili korišćenje priloženog `pom.xml` iz IDE-a).

```bash
cd backend
mvn spring-boot:run
```

Backend se pokreće na `http://localhost:8080`. Baza (H2, fajl `backend/data/gymbooking`) se automatski puni test podacima pri prvom pokretanju (`DataSeeder`):

| Nalog | Email | Lozinka |
|---|---|---|
| Administrator | `admin@teretana.rs` | `admin123` |
| Korisnik | `marko@primer.rs` | `marko123` |

H2 konzola: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/gymbooking`, korisnik `sa`, bez lozinke).

Pokretanje testova:

```bash
cd backend
mvn test
```

## Pokretanje frontend-a

Zahteva Node.js 18+.

```bash
cd frontend
npm install
npm run dev
```

Frontend se pokreće na `http://localhost:5173` i komunicira sa backend-om na `http://localhost:8080/api`.

## Struktura repozitorijuma

```
backend/    Spring Boot REST API (entiteti, repozitorijumi, servisi, kontroleri, security, testovi)
frontend/   React aplikacija (Vite)
docs/       Dokumentacija projekta (.docx) i dijagrami
```
