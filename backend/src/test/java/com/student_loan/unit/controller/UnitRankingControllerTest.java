package com.student_loan.unit.controller;

import com.student_loan.controller.RankingController;
import com.student_loan.dtos.RankingDTO;
import com.student_loan.service.RankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnitRankingControllerTest {

    @Mock
    private RankingService rankingService;

    @InjectMocks
    private RankingController rankingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("GET /api/ranking - returns list of rankings with OK status")
    void getAllRankings_Success() {
        RankingDTO dto1 = new RankingDTO(1L, "Alice", 4.2, 0);
        RankingDTO dto2 = new RankingDTO(2L, "Bob", 3.9, 1);
        List<RankingDTO> expectedList = Arrays.asList(dto1, dto2);

        when(rankingService.getRanking()).thenReturn(expectedList);
        ResponseEntity<List<RankingDTO>> response = rankingController.getAllRankings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(2, response.getBody().size(), "Response should contain two elements");
        assertEquals(expectedList, response.getBody(), "Response body should match expected list");

        verify(rankingService, times(1)).getRanking();
    }

    @Test
    @DisplayName("GET /api/ranking - returns empty list when no rankings")
    void getAllRankings_EmptyList() {
        when(rankingService.getRanking()).thenReturn(Collections.emptyList());
        ResponseEntity<List<RankingDTO>> response = rankingController.getAllRankings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        assertTrue(response.getBody().isEmpty(), "Response body should be empty");

        verify(rankingService, times(1)).getRanking();
    }
}