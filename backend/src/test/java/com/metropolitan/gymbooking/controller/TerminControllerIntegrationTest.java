package com.metropolitan.gymbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metropolitan.gymbooking.dto.LoginRequest;
import com.metropolitan.gymbooking.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integracioni test CRUD operacija, RBAC pravila i validacije kapaciteta
 * za termine, preko pravih HTTP poziva i JWT tokena.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TerminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void zakazivanjeTermina_uProslosti_vracaBadRequest() throws Exception {
        String adminToken = login("admin@teretana.rs", "admin123");
        long trenerId = createTrener(adminToken);
        long treningId = createTrening(adminToken);

        String body = String.format(
                "{\"datumVreme\":\"2020-01-01T10:00:00\",\"kapacitet\":5,\"treningId\":%d,\"trenerId\":%d}",
                treningId, trenerId);

        mockMvc.perform(post("/api/termini")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void izmenaTermina_kapacitetIspodBrojaPotvrdjenihRezervacija_vracaBadRequest() throws Exception {
        String adminToken = login("admin@teretana.rs", "admin123");
        long trenerId = createTrener(adminToken);
        long treningId = createTrening(adminToken);
        long terminId = createTermin(adminToken, trenerId, treningId, 2);

        String tokenA = registerAndLogin("kapacitetA@primer.rs", "lozinka123");
        String tokenB = registerAndLogin("kapacitetB@primer.rs", "lozinka123");
        rezervisi(tokenA, terminId);
        rezervisi(tokenB, terminId);

        String smanjiKapacitet = String.format(
                "{\"datumVreme\":\"2027-02-01T10:00:00\",\"kapacitet\":1,\"treningId\":%d,\"trenerId\":%d}",
                treningId, trenerId);

        mockMvc.perform(put("/api/termini/" + terminId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(smanjiKapacitet))
                .andExpect(status().isBadRequest());
    }

    @Test
    void brisanjeTermina_kaoObicanKorisnik_vracaForbidden() throws Exception {
        String adminToken = login("admin@teretana.rs", "admin123");
        long trenerId = createTrener(adminToken);
        long treningId = createTrening(adminToken);
        long terminId = createTermin(adminToken, trenerId, treningId, 5);

        String korisnikToken = registerAndLogin("brisanje@primer.rs", "lozinka123");

        mockMvc.perform(delete("/api/termini/" + terminId)
                        .header("Authorization", "Bearer " + korisnikToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void pregledBuducihTermina_vidljivSvimAutentifikovanimKorisnicima() throws Exception {
        String korisnikToken = registerAndLogin("pregled.termina@primer.rs", "lozinka123");

        mockMvc.perform(get("/api/termini")
                        .header("Authorization", "Bearer " + korisnikToken))
                .andExpect(status().isOk());
    }

    private void rezervisi(String token, long terminId) throws Exception {
        mockMvc.perform(post("/api/rezervacije")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"terminId\":" + terminId + "}"))
                .andExpect(status().isOk());
    }

    private String registerAndLogin(String email, String lozinka) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setIme("Test");
        request.setPrezime("Korisnik");
        request.setEmail(email);
        request.setLozinka(lozinka);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private String login(String email, String lozinka) throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setLozinka(lozinka);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private long createTrener(String adminToken) throws Exception {
        String body = "{\"ime\":\"Test\",\"prezime\":\"Trener\",\"specijalnost\":\"Opšta priprema\",\"biografija\":\"Bio\"}";
        MvcResult result = mockMvc.perform(post("/api/treneri")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private long createTrening(String adminToken) throws Exception {
        String body = "{\"naziv\":\"Test trening\",\"opis\":\"Opis\",\"trajanjeMinuta\":45}";
        MvcResult result = mockMvc.perform(post("/api/treninzi")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private long createTermin(String adminToken, long trenerId, long treningId, int kapacitet) throws Exception {
        String body = String.format(
                "{\"datumVreme\":\"2027-01-15T10:00:00\",\"kapacitet\":%d,\"treningId\":%d,\"trenerId\":%d}",
                kapacitet, treningId, trenerId);
        MvcResult result = mockMvc.perform(post("/api/termini")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
