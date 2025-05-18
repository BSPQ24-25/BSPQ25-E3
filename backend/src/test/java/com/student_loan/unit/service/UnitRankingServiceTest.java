package com.student_loan.unit.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.student_loan.dtos.RankingDTO;
import com.student_loan.repository.UserRepository;
import com.student_loan.service.RankingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class UnitRankingServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RankingService rankingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRanking_ReturnsRankingList() {
        RankingDTO dto1 = new RankingDTO(1L, "Alice", 4.5, 0);
        RankingDTO dto2 = new RankingDTO(2L, "Bob", 3.8, 1);
        List<RankingDTO> expected = Arrays.asList(dto1, dto2);

        when(userRepository.findAllRanked()).thenReturn(expected);
        List<RankingDTO> result = rankingService.getRanking();

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return two ranking entries");
        assertEquals(expected, result, "Returned list should match expected");

        verify(userRepository, times(1)).findAllRanked();
    }

    @Test
    void testGetRanking_EmptyList() {
        when(userRepository.findAllRanked()).thenReturn(Collections.emptyList());
        List<RankingDTO> result = rankingService.getRanking();

        assertNotNull(result, "Result should not be null even if empty");
        assertTrue(result.isEmpty(), "Result list should be empty");

        verify(userRepository, times(1)).findAllRanked();
    }
}