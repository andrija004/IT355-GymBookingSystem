package com.metropolitan.gymbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metropolitan.gymbooking.config.SecurityConfig;
import com.metropolitan.gymbooking.dto.TrenerDto;
import com.metropolitan.gymbooking.security.JwtService;
import com.metropolitan.gymbooking.service.TrenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Primer slice testa (@WebMvcTest) koji proverava REST i RBAC ponašanje
 * TrenerController-a bez podizanja celog Spring konteksta.
 */
@WebMvcTest(TrenerController.class)
@Import(SecurityConfig.class)
class TrenerControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrenerService trenerService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void findAll_bezAutentifikacije_vracaForbidden() throws Exception {
        mockMvc.perform(get("/api/treneri"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void findAll_saAutentifikovanimKorisnikom_vracaListuTrenera() throws Exception {
        given(trenerService.findAll()).willReturn(
                List.of(new TrenerDto(1L, "Nikola", "Nikolić", "Kondiciona priprema", "Bio")));

        mockMvc.perform(get("/api/treneri"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ime").value("Nikola"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_kaoAdministrator_vracaKreiranogTrenera() throws Exception {
        TrenerDto dto = new TrenerDto(null, "Jovana", "Jovanović", "Joga", "Bio");
        TrenerDto sacuvan = new TrenerDto(1L, "Jovana", "Jovanović", "Joga", "Bio");
        given(trenerService.create(any(TrenerDto.class))).willReturn(sacuvan);

        mockMvc.perform(post("/api/treneri")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "KORISNIK")
    void create_kaoObicanKorisnik_vracaForbidden() throws Exception {
        TrenerDto dto = new TrenerDto(null, "Jovana", "Jovanović", "Joga", "Bio");

        mockMvc.perform(post("/api/treneri")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor csrf() {
        return org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf();
    }
}
