package com.student_loan.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.student_loan.dtos.RankingDTO;
import com.student_loan.repository.UserRepository;

@Service
public class RankingService {
    private final UserRepository userRepo;

    public RankingService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<RankingDTO> getRanking() {
        return userRepo.findAllRanked();
    }
}

