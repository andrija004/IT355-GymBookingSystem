package com.metropolitan.gymbooking.controller;

import com.fasterxml.jackson.databind.JsonNode;
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
 * Integracioni test kompletnog toka rezervacije, uključujući posebnu
 * funkcionalnost projekta (lista čekanja / waitlist) preko pravih HTTP
 * poziva i JWT autentifikacije.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RezervacijaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void kompletanTokRezervacije_saListomCekanjaIPromocijom() throws Exception {
        String adminToken = login("admin@teretana.rs", "admin123");

        Long trenerId = createTrener(adminToken);
        Long treningId = createTrening(adminToken);
        Long terminId = createTermin(adminToken, trenerId, treningId, 2);

        String tokenA = registerAndLogin("korisnikA@waitlist.rs", "lozinka123");
        String tokenB = registerAndLogin("korisnikB@waitlist.rs", "lozinka123");
        String tokenC = registerAndLogin("korisnikC@waitlist.rs", "lozinka123");

        rezervisi(tokenA, terminId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("POTVRDJENA"));

        rezervisi(tokenB, terminId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("POTVRDJENA"));

        rezervisi(tokenC, terminId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NA_CEKANJU"));

        // Ponovni pokušaj B-a mora biti odbijen jer već ima aktivnu rezervaciju za ovaj termin
        rezervisi(tokenB, terminId).andExpect(status().isBadRequest());

        Long idA = getMojaRezervacijaId(tokenA, terminId);
        mockMvc.perform(delete("/api/rezervacije/" + idA)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/rezervacije/moje")
                        .header("Authorization", "Bearer " + tokenC))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("POTVRDJENA"));
    }

    @Test
    void rezervisi_bezAutentifikacije_vracaForbidden() throws Exception {
        mockMvc.perform(post("/api/rezervacije")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"terminId\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void otkazivanjeTudjeRezervacije_vracaForbidden() throws Exception {
        String adminToken = login("admin@teretana.rs", "admin123");
        Long trenerId = createTrener(adminToken);
        Long treningId = createTrening(adminToken);
        Long terminId = createTermin(adminToken, trenerId, treningId, 5);

        String tokenA = registerAndLogin("vlasnik@primer.rs", "lozinka123");
        String tokenB = registerAndLogin("napadac@primer.rs", "lozinka123");

        Long rezervacijaId = extractRezervacijaId(rezervisi(tokenA, terminId).andReturn());

        mockMvc.perform(delete("/api/rezervacije/" + rezervacijaId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());
    }

    private org.springframework.test.web.servlet.ResultActions rezervisi(String token, Long terminId) throws Exception {
        return mockMvc.perform(post("/api/rezervacije")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"terminId\":" + terminId + "}"));
    }

    private Long getMojaRezervacijaId(String token, Long terminId) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/rezervacije/moje")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode arr = objectMapper.readTree(result.getResponse().getContentAsString());
        for (JsonNode node : arr) {
            if (node.get("termin").get("id").asLong() == terminId) {
                return node.get("id").asLong();
            }
        }
        throw new IllegalStateException("Rezervacija nije pronađena");
    }

    private Long extractRezervacijaId(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
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

    private Long createTrener(String adminToken) throws Exception {
        String body = "{\"ime\":\"Test\",\"prezime\":\"Trener\",\"specijalnost\":\"Opšta priprema\",\"biografija\":\"Bio\"}";
        MvcResult result = mockMvc.perform(post("/api/treneri")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Long createTrening(String adminToken) throws Exception {
        String body = "{\"naziv\":\"Test trening\",\"opis\":\"Opis\",\"trajanjeMinuta\":45}";
        MvcResult result = mockMvc.perform(post("/api/treninzi")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Long createTermin(String adminToken, Long trenerId, Long treningId, int kapacitet) throws Exception {
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
