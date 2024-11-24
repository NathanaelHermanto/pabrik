package com.nathancorp.pabrik.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nathancorp.pabrik.dto.request.CreatePaddyRequest;
import com.nathancorp.pabrik.model.Paddy;
import com.nathancorp.pabrik.model.Storage;
import com.nathancorp.pabrik.service.JwtService;
import com.nathancorp.pabrik.service.PaddyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaddyController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaddyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaddyService paddyService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @Test
    void testGetPaddies() throws Exception {
        List<Paddy> paddies = List.of(new Paddy(), new Paddy());
        Pageable pageable = PageRequest.of(0, 5);
        when(paddyService.getAllPaddies(any())).thenReturn(new PageImpl<>(paddies, pageable, paddies.size()));

        mockMvc.perform(get("/api/v1/paddy")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(paddies.size()))
                .andExpect(jsonPath("$.totalElements").value(paddies.size()));

        verify(paddyService, times(1)).getAllPaddies(any());
    }

    @Test
    void testGetAvailablePaddies() throws Exception {
        Paddy mockPaddy = new Paddy(UUID.randomUUID(), 500.0, 20.0,
                "Supplier A", Storage.STORAGE_1, LocalDateTime.now(),0.0);
        Paddy mockPaddy1 = new Paddy(UUID.randomUUID(), 500.0, 20.0,
                "Supplier A", Storage.STORAGE_1, LocalDateTime.now(),0.0);
        List<Paddy> paddies = List.of(
                mockPaddy,
                mockPaddy1
        );
        Pageable pageable = PageRequest.of(0, 5);

        when(paddyService.getAllAvailablePaddies(any()))
                .thenReturn(new PageImpl<>(paddies, pageable, paddies.size()));

        mockMvc.perform(get("/api/v1/paddy/available")
                        .param("size", "5")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(paddies.size()))
                .andExpect(jsonPath("$.totalElements").value(paddies.size()));

        verify(paddyService, times(1)).getAllAvailablePaddies(any());
    }

    @Test
    void testGetPaddyById() throws Exception {
        UUID id = UUID.randomUUID();
        Paddy paddy = new Paddy();
        paddy.setId(id);
        when(paddyService.getPaddyById(id.toString())).thenReturn(paddy);

        mockMvc.perform(get("/api/v1/paddy/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
        verify(paddyService, times(1)).getPaddyById(id.toString());
    }

    @Test
    void testCreatePaddy() throws Exception {
        CreatePaddyRequest request = new CreatePaddyRequest(500.0, 100.0, "Supplier A");

        Paddy created = Paddy.builder()
                .id(UUID.randomUUID())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .supplier(request.getSupplier())
                .storage(Storage.STORAGE_1)
                .processedQuantity(0.0)
                .purchaseDate(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS))
                .supplier(request.getSupplier())
                .build();

        when(paddyService.createPaddy(request.getQuantity(), request.getPrice(), request.getSupplier())).thenReturn(created);

        mockMvc.perform(post("/api/v1/paddy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId().toString()))
                .andExpect(jsonPath("$.price").value(request.getPrice()))
                .andExpect(jsonPath("$.quantity").value(request.getQuantity()))
                .andExpect(jsonPath("$.supplier").value(request.getSupplier()))
                .andExpect(jsonPath("$.storage").value(Storage.STORAGE_1.name()))
                .andExpect(jsonPath("$.processedQuantity").value(0.0))
                .andExpect(jsonPath("$.supplier").value(request.getSupplier()));

        verify(paddyService, times(1)).createPaddy(request.getQuantity(), request.getPrice(), request.getSupplier());
    }

    @Test
    void testDeletePaddy() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(paddyService).deletePaddy(any(UUID.class));

        mockMvc.perform(delete("/api/v1/paddy/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Paddy "+ id + " deleted"));

        verify(paddyService, times(1)).deletePaddy(id);
    }
}

