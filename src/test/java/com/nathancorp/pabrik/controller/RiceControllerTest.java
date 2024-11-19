package com.nathancorp.pabrik.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nathancorp.pabrik.dto.request.CreateRiceRequest;
import com.nathancorp.pabrik.model.Batch;
import com.nathancorp.pabrik.model.Rice;
import com.nathancorp.pabrik.model.RiceType;
import com.nathancorp.pabrik.service.JwtService;
import com.nathancorp.pabrik.service.RiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class RiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RiceService riceService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    private Batch mockBatch;

    private Rice mockRice;

    @BeforeEach
    void setUp() {
        mockBatch = Batch.builder()
                .id(UUID.randomUUID())
                .producedQuantity(500.0)
                .isProcessed(true)
                .quantity(800.0)
                .processingDate(LocalDateTime.now())
                .build();

        mockRice = Rice.builder()
                .riceType(RiceType.GRADE_A)
                .id(UUID.randomUUID())
                .batch(mockBatch)
                .quantity(500.0)
                .productionDate(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetRice() throws Exception {
        List<Rice> riceList = List.of(new Rice(), new Rice());
        when(riceService.getAllRice()).thenReturn(riceList);

        mockMvc.perform(get("/api/v1/rice")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(riceList.size()))
                .andExpect(jsonPath("$.totalElements").value(riceList.size()));

        verify(riceService, times(1)).getAllRice();
    }

    @Test
    void testGetRiceById() throws Exception {
        when(riceService.getRiceById(mockRice.getId().toString())).thenReturn(mockRice);

        mockMvc.perform(get("/api/v1/rice/{id}", mockRice.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockRice.getId().toString()));
        verify(riceService, times(1)).getRiceById(mockRice.getId().toString());
    }

    @Test
    void testCreateRice() throws Exception {
        CreateRiceRequest createRiceRequest = CreateRiceRequest.builder()
                .quantity(500.0)
                .build();

        when(riceService.createRice(createRiceRequest.getQuantity())).thenReturn(mockRice);

        mockMvc.perform(post("/api/v1/rice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRiceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockRice.getId().toString()))
                .andExpect(jsonPath("$.quantity").value(mockRice.getQuantity()));
    }

    @Test
    void testDeleteRiceById() throws Exception {
        mockMvc.perform(delete("/api/v1/rice/{id}", mockRice.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Rice "+ mockRice.getId().toString() + " deleted"));
    }

}

