package com.nathancorp.pabrik.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nathancorp.pabrik.exception.InvalidPriceException;
import com.nathancorp.pabrik.exception.InvalidQuantityException;
import com.nathancorp.pabrik.exception.PaddyNotAvailableForProcessingException;
import com.nathancorp.pabrik.model.Batch;
import com.nathancorp.pabrik.model.Paddy;
import com.nathancorp.pabrik.model.Storage;
import com.nathancorp.pabrik.repository.PaddyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.*;

class PaddyServiceTest {

    @Mock
    private PaddyRepository paddyRepository;

    @InjectMocks
    private PaddyService paddyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePaddy() {
        Paddy mockPaddy = new Paddy(UUID.randomUUID(), 500.0, 20.0,
                "Supplier A", Storage.STORAGE_1, LocalDateTime.now(), new ArrayList<>(), 0.0);
        when(paddyRepository.save(any(Paddy.class))).thenReturn(mockPaddy);

        Paddy createdPaddy = paddyService.createPaddy(500.0, 20.0, "Supplier A");
        assertEquals(mockPaddy.getId(), createdPaddy.getId());
        verify(paddyRepository, times(1)).save(any(Paddy.class));
    }

    @Test
    void testDeletePaddy() {
        UUID paddyId = UUID.randomUUID();
        when(paddyRepository.existsById(paddyId)).thenReturn(true);

        paddyService.deletePaddy(paddyId);
        verify(paddyRepository, times(1)).deleteById(paddyId);
    }

    @Test
    void testDeletePaddy_NotFound() {
        UUID paddyId = UUID.randomUUID();
        when(paddyRepository.existsById(paddyId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> paddyService.deletePaddy(paddyId));
    }

    @Test
    void testGetPaddyById() {
        UUID paddyId = UUID.randomUUID();
        Paddy mockPaddy = new Paddy(paddyId, 500.0, 20.0,
                "Supplier A", Storage.STORAGE_1, LocalDateTime.now(), new ArrayList<>(), 0.0);
        when(paddyRepository.findById(paddyId)).thenReturn(Optional.of(mockPaddy));

        Paddy fetchedPaddy = paddyService.getPaddyById(paddyId.toString());
        assertEquals(mockPaddy.getId(), fetchedPaddy.getId());
    }

    @Test
    void testGetPaddyById_NotFound() {
        UUID paddyId = UUID.randomUUID();
        when(paddyRepository.findById(paddyId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> paddyService.getPaddyById(paddyId.toString()));
    }

    @Test
    void testGetAllAvailablePaddies() {
        Paddy paddy1 = new Paddy(UUID.randomUUID(), 500.0, 20.0,
                "Supplier A", Storage.STORAGE_1, LocalDateTime.now(), new ArrayList<>(), 0.0);
        Paddy paddy2 = new Paddy(UUID.randomUUID(), 1000.0, 25.0,
                "Supplier B", Storage.STORAGE_1, LocalDateTime.now(), new ArrayList<>(), 100);
        when(paddyRepository.findAll()).thenReturn(List.of(paddy1, paddy2));

        List<Paddy> availablePaddies = paddyService.getAllAvailablePaddies();
        assertEquals(2, availablePaddies.size());
    }

    @Test
    void testGetAvailablePaddyByIdForBatch() {
        UUID paddyId = UUID.randomUUID();
        Paddy mockPaddy = new Paddy(paddyId, 500.0, 20.0,
                "Supplier A", Storage.STORAGE_1, LocalDateTime.now(), new ArrayList<>(), 100);
        when(paddyRepository.findById(paddyId)).thenReturn(Optional.of(mockPaddy));

        Paddy availablePaddy = paddyService.getAvailablePaddyByIdForBatch(paddyId.toString(), 300.0);
        assertEquals(mockPaddy, availablePaddy);
    }

    @Test
    void testGetAvailablePaddyByIdForBatch_NotAvailable() {
        UUID paddyId = UUID.randomUUID();
        Paddy mockPaddy = new Paddy(paddyId, 500.0, 20.0,
                "Supplier A", Storage.STORAGE_1, LocalDateTime.now(), new ArrayList<>(), 500.0);
        when(paddyRepository.findById(paddyId)).thenReturn(Optional.of(mockPaddy));

        assertThrows(PaddyNotAvailableForProcessingException.class,
                () -> paddyService.getAvailablePaddyByIdForBatch(paddyId.toString(), 300.0));
    }

    @Test
    void testUpdatePaddiesProcessedQuantity() {
        UUID paddyId = UUID.randomUUID();
        Paddy mockPaddy = new Paddy(paddyId, 500.0, 20.0, "Supplier A", Storage.STORAGE_1, LocalDateTime.now(), new ArrayList<>(), 100);
        Batch mockBatch = new Batch(UUID.randomUUID(), 250.0, Storage.STORAGE_2, LocalDateTime.now(),
                true, new ArrayList<>(), new ArrayList<>(), 200.0, null);

        when(paddyRepository.findById(paddyId)).thenReturn(Optional.of(mockPaddy));

        Map<String, Double> paddyAndQuantity = Map.of(paddyId.toString(), 200.0);
        paddyService.updatePaddiesProcessedQuantity(paddyAndQuantity, mockBatch);

        assertEquals(300.0, mockPaddy.getProcessedQuantity());
        verify(paddyRepository, times(1)).save(mockPaddy);
    }

    @Test
    void testCreatePaddy_NegativePrice() {
        assertThrows(InvalidPriceException.class, () ->
                paddyService.createPaddy(500.0, -20.0, "Supplier A"));
    }

    @Test
    void testCreatePaddy_NegativeQuantity() {
        assertThrows(InvalidQuantityException.class, () ->
                paddyService.createPaddy(-500.0, 20.0, "Supplier A"));
    }

    @Test
    void testDeletePaddy_WithDependencies() {
        UUID paddyId = UUID.randomUUID();
        // Assuming paddies with dependencies should not be deletable
        when(paddyRepository.existsById(paddyId)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("Cannot delete due to dependencies")).when(paddyRepository).deleteById(paddyId);

        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> paddyService.deletePaddy(paddyId));
        assertEquals("Cannot delete due to dependencies", exception.getMessage());
    }

    @Test
    void testGetAvailablePaddyByIdForBatch_InvalidQuantity() {
        UUID paddyId = UUID.randomUUID();
        Paddy mockPaddy = new Paddy(paddyId, 500.0, 20.0, "Supplier A", Storage.STORAGE_1, LocalDateTime.now(), new ArrayList<>(), 460);
        when(paddyRepository.findById(paddyId)).thenReturn(Optional.of(mockPaddy));

        assertThrows(PaddyNotAvailableForProcessingException.class, () -> paddyService.getAvailablePaddyByIdForBatch(paddyId.toString(), 50.0));
    }

}
