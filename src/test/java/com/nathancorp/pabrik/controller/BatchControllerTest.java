package com.nathancorp.pabrik.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nathancorp.pabrik.dto.request.CreateBatchRequest;
import com.nathancorp.pabrik.dto.request.CreatePaddyRequest;
import com.nathancorp.pabrik.dto.request.UpdateBatchRequest;
import com.nathancorp.pabrik.model.Batch;
import com.nathancorp.pabrik.model.Paddy;
import com.nathancorp.pabrik.model.Storage;
import com.nathancorp.pabrik.service.BatchService;
import com.nathancorp.pabrik.service.JwtService;
import com.nathancorp.pabrik.service.PaddyService;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BatchController.class)
@AutoConfigureMockMvc(addFilters = false)
class BatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaddyService paddyService;

    @MockBean
    private BatchService batchService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    private Batch mockBatch;

    @BeforeEach
    void setUp() {
        mockBatch = Batch.builder()
                .id(UUID.randomUUID())
                .producedQuantity(500.0)
                .isProcessed(true)
                .quantity(800.0)
                .processingDate(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetBatch() throws Exception {
        List<Batch> batches = List.of(new Batch(), new Batch());
        Pageable pageable = PageRequest.of(0, 5);

        when(batchService.getAllBatch(any())).thenReturn(new PageImpl<>(batches, pageable, batches.size()));

        mockMvc.perform(get("/api/v1/batch")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(batches.size()))
                .andExpect(jsonPath("$.totalElements").value(batches.size()));

        verify(batchService, times(1)).getAllBatch(any());
    }

    @Test
    void testGetBatchById() throws Exception {
        UUID id = UUID.randomUUID();
        Batch batch = new Batch();
        batch.setId(id);
        when(batchService.getBatchById(id.toString())).thenReturn(batch);

        mockMvc.perform(get("/api/v1/batch/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
        verify(batchService, times(1)).getBatchById(id.toString());
    }

    @Test
    void testCreateBatch() throws Exception {
        Map<String, Double> paddyAndQuantity = Map.of("paddy-1", 200.0);
        when(batchService.createBatch(any(), any())).thenReturn(mockBatch);

        mockMvc.perform(post("/api/v1/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "paddyAndQuantity", paddyAndQuantity,
                                "producedQuantity", 500.0
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockBatch.getId().toString()))
                .andExpect(jsonPath("$.producedQuantity").value(mockBatch.getProducedQuantity()))
                .andExpect(jsonPath("$.processed").value(mockBatch.isProcessed()))
                .andExpect(jsonPath("$.paddiesAndQuantity").value(mockBatch.getPaddiesAndQuantity()));
    }

    @Test
    void testPatchBatchStatusById() throws Exception {
        mockBatch.setProcessed(true);
        mockBatch.setProducedQuantity(500.0);

        UpdateBatchRequest updateBatchRequest = UpdateBatchRequest.builder()
                .isProcessed(true)
                .producedQuantity(500.0)
                .build();

        when(batchService.updateBatchStatus(mockBatch.getId().toString(), true, 500.0))
                .thenReturn(mockBatch);

        mockMvc.perform(patch("/api/v1/batch/status/" + mockBatch.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBatchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockBatch.getId().toString()))
                .andExpect(jsonPath("$.producedQuantity").value(mockBatch.getProducedQuantity()))
                .andExpect(jsonPath("$.processed").value(mockBatch.isProcessed()));
    }

}

