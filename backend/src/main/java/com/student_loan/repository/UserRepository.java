package com.student_loan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.student_loan.dtos.RankingDTO;
import com.student_loan.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
      SELECT new com.student_loan.dtos.RankingDTO(
        u.id,
        u.name,
        u.averageRating,
        u.penalties
      )
      FROM User u
      ORDER BY u.averageRating DESC, u.penalties ASC
    """)
    List<RankingDTO> findAllRanked();

    User findByEmail(String email);
}
