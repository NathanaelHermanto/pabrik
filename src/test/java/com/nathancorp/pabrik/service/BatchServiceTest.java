package com.nathancorp.pabrik.service;

import com.nathancorp.pabrik.exception.NegativeQuantityException;
import com.nathancorp.pabrik.exception.PaddyNotAvailableForProcessingException;
import com.nathancorp.pabrik.model.Batch;
import com.nathancorp.pabrik.model.Paddy;
import com.nathancorp.pabrik.model.Rice;
import com.nathancorp.pabrik.model.Storage;
import com.nathancorp.pabrik.repository.BatchRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BatchServiceTest {

    @Mock
    private RiceService riceService;

    @Mock
    private PaddyService paddyService;

    @Mock
    private BatchRepository batchRepository;

    @InjectMocks
    private BatchService batchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBatch() {
        List<Batch> mockBatchList = List.of(
                Batch.builder()
                        .id(UUID.randomUUID())
                        .quantity(100.0)
                        .processingDate(LocalDateTime.now())
                        .build(),
                Batch.builder()
                        .id(UUID.randomUUID())
                        .quantity(10.0)
                        .processingDate(LocalDateTime.now())
                        .build()
        );

        when(batchRepository.findAll()).thenReturn(mockBatchList);

        List<Batch> batchList = batchService.getAllBatch();

        assertEquals(2, batchList.size());
    }

    @Test
    void testGetBatchById() {
        Batch mockBatch = Batch.builder()
                .id(UUID.randomUUID())
                .quantity(100.0)
                .processingDate(LocalDateTime.now())
                .build();

        when(batchRepository.findById(any())).thenReturn(Optional.ofNullable(mockBatch));

        assert mockBatch != null;
        Batch batch = batchService.getBatchById(mockBatch.getId().toString());

        assertEquals(batch.getId(), mockBatch.getId());
        assertEquals(batch.getQuantity(), mockBatch.getQuantity());
    }

    @Test
    void testCreateBatch_Success() {
        Map<String, Double> paddyAndQuantity = Map.of("paddy1", 100.0, "paddy2", 200.0);

        Paddy mockPaddy1 = new Paddy(UUID.randomUUID(), 500.0, 200.0, "Supplier A",
                Storage.STORAGE_1, LocalDateTime.now(), new ArrayList<>(), 50.0);
        Paddy mockPaddy2 = new Paddy(UUID.randomUUID(), 1000.0, 300.0, "Supplier B",
                Storage.STORAGE_1, LocalDateTime.now(), new ArrayList<>(), 100.0);

        when(paddyService.getAvailablePaddyByIdForBatch("paddy1", 100.0)).thenReturn(mockPaddy1);
        when(paddyService.getAvailablePaddyByIdForBatch("paddy2", 200.0)).thenReturn(mockPaddy2);

        Batch mockBatch = Batch.builder()
                .id(UUID.randomUUID())
                .producedQuantity(250.0)
                .processingDate(LocalDateTime.now())
                .isProcessed(true)
                .storage(Storage.STORAGE_2)
                .paddies(List.of(mockPaddy1, mockPaddy2))
                .paddiesAndQuantity(paddyAndQuantity)
                .quantity(300.0)
                .build();

        Rice mockRice = Rice.builder()
                .id(UUID.randomUUID())
                .batch(mockBatch)
                .productionDate(LocalDateTime.now())
                .quantity(250.0)
                .storage(Storage.STORAGE_3)
                .build();

        when(batchRepository.save(any(Batch.class))).thenReturn(mockBatch);
        when(riceService.createRice(any(Batch.class))).thenReturn(mockRice);

        Batch createdBatch = batchService.createBatch(paddyAndQuantity, 250.0);

        assertNotNull(createdBatch);
        assertTrue(createdBatch.isProcessed());
        assertEquals(250.0, createdBatch.getProducedQuantity());
        verify(batchRepository, times(1)).save(any(Batch.class));
        verify(riceService, times(1)).createRice(any(Batch.class));
        verify(paddyService, times(1)).updatePaddiesProcessedQuantity(paddyAndQuantity, mockBatch);
    }

    @Test
    void testCreateBatch_InvalidQuantity_ThrowsException() {
        Map<String, Double> paddyAndQuantity = Map.of("paddy1", -100.0);

        NegativeQuantityException exception = assertThrows(NegativeQuantityException.class,
                () -> batchService.createBatch(paddyAndQuantity, 250.0));

        assertEquals("Invalid quantity", exception.getMessage());
        verifyNoInteractions(batchRepository, riceService, paddyService);
    }

    @Test
    void testCreateBatch_InsufficientPaddyQuantity_ThrowsException() {
        Map<String, Double> paddyAndQuantity = Map.of("paddy1", 500.0);

        when(paddyService.getAvailablePaddyByIdForBatch("paddy1", 500.0))
                .thenThrow(new PaddyNotAvailableForProcessingException("Paddy not available"));

        PaddyNotAvailableForProcessingException exception = assertThrows(PaddyNotAvailableForProcessingException.class,
                () -> batchService.createBatch(paddyAndQuantity, 250.0));

        assertEquals("Paddy not available", exception.getMessage());
        verifyNoInteractions(batchRepository, riceService);
    }

    @Test
    void testCreateBatch_ZeroProducedQuantity_ThrowsException() {
        Map<String, Double> paddyAndQuantity = Map.of("paddy1", 100.0);

        assertThrows(NegativeQuantityException.class, () -> batchService.createBatch(paddyAndQuantity, 0.0));
        verifyNoInteractions(batchRepository, riceService, paddyService);
    }

    @Test
    void testCreateBatch_EmptyPaddyList_ThrowsException() {
        Map<String, Double> paddyAndQuantity = Collections.emptyMap();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> batchService.createBatch(paddyAndQuantity, 250.0));

        assertEquals("Paddy and quantity is empty", exception.getMessage());
        verifyNoInteractions(batchRepository, riceService, paddyService);
    }

    @Test
    void testUpdateBatchStatus_Success() {
        UUID batchId = UUID.randomUUID();
        Batch mockBatch = new Batch();
        mockBatch.setId(batchId);
        mockBatch.setProducedQuantity(0.0);
        mockBatch.setProcessed(false);

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(mockBatch));
        when(batchRepository.save(any(Batch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Batch updatedBatch = batchService.updateBatchStatus(batchId.toString(), true, 500.0);

        assertNotNull(updatedBatch);
        assertTrue(updatedBatch.isProcessed());
        assertEquals(500.0, updatedBatch.getProducedQuantity());
        verify(batchRepository, times(1)).save(mockBatch);
    }

    @Test
    void testUpdateBatchStatus_InvalidProducedQuantity_ThrowsException() {
        UUID batchId = UUID.randomUUID();

        NegativeQuantityException exception = assertThrows(NegativeQuantityException.class,
                () -> batchService.updateBatchStatus(batchId.toString(), true, -500.0));

        assertEquals("Invalid produced quantity, should be greater than 0", exception.getMessage());
        verifyNoInteractions(batchRepository);
    }

    @Test
    void testUpdateBatchStatus_BatchNotFound_ThrowsException() {
        UUID batchId = UUID.randomUUID();
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> batchService.updateBatchStatus(batchId.toString(), true, 500.0));

        assertEquals("Batch not found", exception.getMessage());
        verify(batchRepository, times(1)).findById(batchId);
        verify(batchRepository, never()).save(any(Batch.class));
    }

    @Test
    void testUpdateBatchStatus_SetNotProcessed() {
        UUID batchId = UUID.randomUUID();
        Batch mockBatch = new Batch();
        mockBatch.setId(batchId);
        mockBatch.setProducedQuantity(1000.0);
        mockBatch.setProcessed(true);

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(mockBatch));
        when(batchRepository.save(any(Batch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Batch updatedBatch = batchService.updateBatchStatus(batchId.toString(), false, 500.0);

        assertNotNull(updatedBatch);
        assertFalse(updatedBatch.isProcessed());
        assertEquals(500.0, updatedBatch.getProducedQuantity());
        verify(batchRepository, times(1)).save(mockBatch);
    }

    @Test
    void testUpdateBatchStatus_ZeroProducedQuantity_ThrowsException() {
        UUID batchId = UUID.randomUUID();
        Batch mockBatch = new Batch();
        mockBatch.setId(batchId);

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(mockBatch));

        NegativeQuantityException exception = assertThrows(NegativeQuantityException.class,
                () -> batchService.updateBatchStatus(batchId.toString(), true, 0.0));

        assertEquals("Invalid produced quantity, should be greater than 0", exception.getMessage());
        verify(batchRepository, never()).save(any(Batch.class));
    }

    @Test
    void testUpdateBatchStatus_InvalidUUID_ThrowsException() {
        String invalidBatchId = "invalid-uuid";

        assertThrows(IllegalArgumentException.class,
                () -> batchService.updateBatchStatus(invalidBatchId, true, 500.0));

        verifyNoInteractions(batchRepository);
    }

}
