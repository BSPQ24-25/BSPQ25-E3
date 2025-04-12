package com.student_loan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.student_loan.model.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Loan findByItem(Long item);
    List<Loan> findByLender(Long lenderId);
    List<Loan> findByBorrower(Long borrowerId);
}