package com.nathancorp.pabrik.service;

import com.nathancorp.pabrik.exception.InvalidQuantityException;
import com.nathancorp.pabrik.model.Batch;
import com.nathancorp.pabrik.model.Rice;
import com.nathancorp.pabrik.model.RiceType;
import com.nathancorp.pabrik.model.Storage;
import com.nathancorp.pabrik.repository.RiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RiceServiceTest {

    @Mock
    private RiceRepository riceRepository;

    @InjectMocks
    private RiceService riceService;

    private Rice mockRice;

    private Rice mockRice1;

    private Rice mockRice2;

    private Batch mockBatch;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockBatch = Batch.builder()
                .id(UUID.randomUUID())
                .producedQuantity(1000.0)
                .processingDate(LocalDateTime.now())
                .isProcessed(true)
                .storage(Storage.STORAGE_2)
                .quantity(1200.0)
                .build();

        mockRice = Rice.builder()
                .id(UUID.randomUUID())
                .batch(mockBatch)
                .quantity(1000.0)
                .productionDate(LocalDateTime.now())
                .storage(Storage.STORAGE_3)
                .build();

        mockRice1 = Rice.builder()
                .id(UUID.randomUUID())
                .quantity(100.0)
                .riceType(RiceType.GRADE_A)
                .productionDate(LocalDateTime.now())
                .storage(Storage.STORAGE_3)
                .batch(null)
                .build();

        mockRice2 = Rice.builder()
                .id(UUID.randomUUID())
                .quantity(200.0)
                .riceType(RiceType.GRADE_A)
                .productionDate(LocalDateTime.now())
                .storage(Storage.STORAGE_3)
                .batch(null)
                .build();
    }

    @Test
    void testGetAllRice() {
        when(riceRepository.findAll()).thenReturn(List.of(mockRice1, mockRice2));

        List<Rice> riceList = riceService.getAllRice();

        assertEquals(2, riceList.size());
    }

    @Test
    void testGetRiceById() {
        when(riceRepository.findById(mockRice1.getId())).thenReturn(java.util.Optional.of(mockRice1));

        Rice rice = riceService.getRiceById(mockRice1.getId().toString());

        assertEquals(mockRice1, rice);
    }

    @Test
    void testCreateRice() {
        when(riceRepository.save(any())).thenReturn(mockRice1);

        Rice rice = riceService.createRice(100.0);

        assertEquals(mockRice1, rice);
    }

    @Test
    void testCreateRice_InvalidQuantity() {
        assertThrows(InvalidQuantityException.class, () -> riceService.createRice(-100.0));
    }

    @Test
    void testCreateRice_Success() {
        when(riceRepository.save(any(Rice.class))).thenReturn(mockRice);

        Rice createdRice = riceService.createRice(mockBatch);

        assertNotNull(createdRice);
        assertEquals(1000.0, createdRice.getQuantity());
        assertEquals(Storage.STORAGE_3, createdRice.getStorage());
        verify(riceRepository, times(1)).save(any(Rice.class));
    }

    @Test
    void testCreateRice_NullBatch() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> riceService.createRice((Batch) null));

        assertEquals("Batch must not be null", exception.getMessage());
        verify(riceRepository, never()).save(any(Rice.class));
    }

    @Test
    void testDeleteRiceByIdSuccess() {
        when(riceRepository.existsById(any())).thenReturn(true);

        riceService.deleteRiceById(mockRice1.getId().toString());

        verify(riceRepository, times(1)).deleteById(mockRice1.getId());
    }

    @Test
    void testDeleteRiceByIdFail() {
        when(riceRepository.existsById(any())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> riceService.deleteRiceById(mockRice1.getId().toString()));
        verify(riceRepository, times(0)).deleteById(mockRice1.getId());
    }

}
