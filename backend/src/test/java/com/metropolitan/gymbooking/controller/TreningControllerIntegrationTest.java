package com.metropolitan.gymbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metropolitan.gymbooking.dto.LoginRequest;
import com.metropolitan.gymbooking.dto.RegisterRequest;
import com.metropolitan.gymbooking.dto.TreningDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integracioni test CRUD operacija i RBAC pravila za treninge, preko
 * pravih HTTP poziva i JWT tokena (bez mokovanja servisnog sloja).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TreningControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crudTokTreninga_kaoAdministrator() throws Exception {
        String adminToken = login("admin@teretana.rs", "admin123");

        TreningDto novi = new TreningDto(null, "Pilates", "Trening za jačanje core mišića", 50);

        MvcResult kreiran = mockMvc.perform(post("/api/treninzi")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novi)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naziv").value("Pilates"))
                .andReturn();

        long id = objectMapper.readTree(kreiran.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/treninzi/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trajanjeMinuta").value(50));

        TreningDto izmenjen = new TreningDto(null, "Pilates napredni", "Izmenjen opis", 60);
        mockMvc.perform(put("/api/treninzi/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(izmenjen)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naziv").value("Pilates napredni"));

        mockMvc.perform(delete("/api/treninzi/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/treninzi/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void kreiranjeTreninga_kaoObicanKorisnik_vracaForbidden() throws Exception {
        String korisnikToken = registerAndLogin("korisnik.trening@primer.rs", "lozinka123");

        TreningDto novi = new TreningDto(null, "Zumba", "Opis", 45);

        mockMvc.perform(post("/api/treninzi")
                        .header("Authorization", "Bearer " + korisnikToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novi)))
                .andExpect(status().isForbidden());
    }

    @Test
    void listaTreninga_bezAutentifikacije_vracaForbidden() throws Exception {
        mockMvc.perform(get("/api/treninzi"))
                .andExpect(status().isForbidden());
    }

    @Test
    void kreiranjeTreninga_saNevalidnimPodacima_vracaBadRequest() throws Exception {
        String adminToken = login("admin@teretana.rs", "admin123");

        TreningDto nevalidan = new TreningDto(null, "", "Opis", -5);

        mockMvc.perform(post("/api/treninzi")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nevalidan)))
                .andExpect(status().isBadRequest());
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
}
