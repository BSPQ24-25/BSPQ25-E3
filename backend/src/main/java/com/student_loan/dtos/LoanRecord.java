package com.student_loan.dtos;


public record LoanRecord(
    Long id,
    Long lender,
    Long borrower,
    Long item,
    String loanDate,
    String estimatedReturnDate,
    String realReturnDate,
    String loanStatus,
    String rating,
    String observations) {}