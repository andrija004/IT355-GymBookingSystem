package com.metropolitan.gymbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metropolitan.gymbooking.dto.LoginRequest;
import com.metropolitan.gymbooking.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_saValidnimPodacima_vracaTokenIStatus200() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setIme("Petar");
        request.setPrezime("Petrović");
        request.setEmail("petar@primer.rs");
        request.setLozinka("lozinka123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("petar@primer.rs"))
                .andExpect(jsonPath("$.uloga").value("KORISNIK"));
    }

    @Test
    void register_saDuplikatEmailom_vracaBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setIme("Petar");
        request.setPrezime("Petrović");
        request.setEmail("duplikat@primer.rs");
        request.setLozinka("lozinka123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_saNevalidnimEmailom_vracaBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setIme("Petar");
        request.setPrezime("Petrović");
        request.setEmail("nije-email");
        request.setLozinka("lozinka123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_saPogresnomLozinkom_vracaUnauthorized() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setIme("Ana");
        register.setPrezime("Anić");
        register.setEmail("ana.login@primer.rs");
        register.setLozinka("ispravnaLozinka");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest();
        login.setEmail("ana.login@primer.rs");
        login.setLozinka("pogresnaLozinka");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}
