package com.student_loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.student_loan.model.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Loan findByItem(Long item);
}