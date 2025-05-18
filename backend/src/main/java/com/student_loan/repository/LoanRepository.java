package com.student_loan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.student_loan.model.Loan;
import com.student_loan.model.Loan.Status;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByItem(Long itemId);
    List<Loan> findByLender(Long lenderId);
    List<Loan> findByBorrower(Long borrowerId);
    List<Loan> findByLenderAndLoanStatus(Long lenderId, Status loanStatus);
    List<Loan> findByBorrowerAndLoanStatus(Long borrowerId, Status loanStatus);
    Optional<Loan> findByBorrowerAndItemAndLoanStatus(Long borrower, Long item, Status loanStatus);
    int countByBorrowerAndLoanStatus(Long borrowerId, Loan.Status loanStatus);
}