package com.student_loan.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.student_loan.dtos.RankingDTO;
import com.student_loan.service.RankingService;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {
    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping
    public ResponseEntity<List<RankingDTO>> getAllRankings() {
        List<RankingDTO> ranking = rankingService.getRanking();
        return ResponseEntity.ok(ranking);
    }
}

