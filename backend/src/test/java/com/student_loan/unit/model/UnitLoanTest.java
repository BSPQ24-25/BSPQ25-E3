package com.student_loan.unit.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.student_loan.model.Loan;
import com.student_loan.model.Loan.Status;
import com.student_loan.repository.LoanRepository;

class UnitLoanTest {

    private LoanRepository repository;
    private Loan loan;

    private Logger logger = LoggerFactory.getLogger(UnitLoanTest.class);
    @BeforeEach
    void setUp() {
        loan = new Loan(1L, 101L, 202L, 303L, new Date(), new Date(), null, Status.IN_USE, 4.5, "Good condition");
        repository = mock(LoanRepository.class);
    }

    @Test
    @DisplayName("Should Add Loan Successfully")
    void testAddLoan() {
        when(repository.save(loan)).thenReturn(loan);

        Loan result = repository.save(loan);

        verify(repository, times(1)).save(loan);
        assertEquals(101L, result.getLender());
        assertEquals(Status.IN_USE, result.getLoanStatus());
    }

    @Test
    @DisplayName("Should Retrieve Loan By ID")
    void testGetLoanById() {
        when(repository.findById(1L)).thenReturn(Optional.of(loan));

        Loan result = repository.findById(1L).orElseThrow(() -> new RuntimeException("Loan not found"));

        assertNotNull(result);
        assertEquals(202L, result.getBorrower());
    }

    @Test
    @DisplayName("Should Update Loan Status")
    void testUpdateLoanStatus() {
        loan.setLoanStatus(Status.RETURNED);

        when(repository.save(loan)).thenReturn(loan);
        Loan result = repository.save(loan);

        verify(repository, times(1)).save(loan);
        assertEquals(Status.RETURNED, result.getLoanStatus());
    }

    @Test
    @DisplayName("Should Throw Exception When Loan Not Found")
    void testGetLoanById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            repository.findById(999L).orElseThrow(() -> new RuntimeException("Loan not found"));
        });

        assertEquals("Loan not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should Get and Set Rating Correctly")
    void testGetSetRating() {
        loan.setRating(3.8);
        assertEquals(3.8, loan.getRating());
    }

    @Test
    @DisplayName("Should Delete Loan Successfully")
    void testDeleteLoan() {
        doNothing().when(repository).delete(loan);

        repository.delete(loan);

        verify(repository, times(1)).delete(loan);
    }

    @Test
    @DisplayName("Should Update Real Return Date")
    void testUpdateRealReturnDate() {
        Date newReturnDate = new Date();
        loan.setRealReturnDate(newReturnDate);

        when(repository.save(loan)).thenReturn(loan);
        Loan result = repository.save(loan);

        verify(repository, times(1)).save(loan);
        assertEquals(newReturnDate, result.getRealReturnDate());
    }

    @Test
    @DisplayName("Should Get and Set Observations Correctly")
    void testGetSetObservations() {
        loan.setObservations("Returned in perfect condition");
        assertEquals("Returned in perfect condition", loan.getObservations());
    }

    @Test
    @DisplayName("Should Update Loan Rating")
    void testUpdateLoanRating() {
        loan.setRating(4.7);

        when(repository.save(loan)).thenReturn(loan);
        Loan result = repository.save(loan);

        verify(repository, times(1)).save(loan);
        assertEquals(4.7, result.getRating());
    }

    @Test
    @DisplayName("Should Update Loan Borrower")
    void testUpdateLoanBorrower() {
        loan.setBorrower(404L);

        when(repository.save(loan)).thenReturn(loan);
        Loan result = repository.save(loan);

        verify(repository, times(1)).save(loan);
        assertEquals(404L, result.getBorrower());
    }

    @Test
    @DisplayName("getEstimatedReturnDate returns the value set")
    void testGetEstimatedReturnDate() {
        Date estimated = new Date(1620000000000L);
        loan.setEstimatedReturnDate(estimated);
        assertEquals(estimated, loan.getEstimatedReturnDate());
    }

    @Test
    @DisplayName("setLoanDate and getLoanDate work correctly")
    void testGetSetLoanDate() {
        Date loanDate = new Date(1630000000000L);
        loan.setLoanDate(loanDate);
        assertEquals(loanDate, loan.getLoanDate());
    }

    @Test
    @DisplayName("toString includes all loan fields and values")
    void testToStringIncludesAllFields() {
        String str = loan.toString();
        logger.info("Loan toString: " + str);
        assertTrue(str.contains("id=" + loan.getId()));
        assertTrue(str.contains("lender=" + loan.getLender()));
        assertTrue(str.contains("borrower=" + loan.getBorrower()));
        assertTrue(str.contains("item=" + loan.getItem()));
        assertTrue(str.contains("loanDate=" + loan.getLoanDate()));
        assertTrue(str.contains("estimatedReturnDate=" + loan.getEstimatedReturnDate()));
        assertTrue(str.contains("realReturnDate=" ));
        assertTrue(str.contains("loanStatus=" + loan.getLoanStatus()));
        assertTrue(str.contains("rating=" + loan.getRating()));
        assertTrue(str.contains("observations='" + loan.getObservations() + "'"));
    }
}
