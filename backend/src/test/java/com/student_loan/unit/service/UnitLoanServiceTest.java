package com.student_loan.unit.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.student_loan.model.Loan;
import com.student_loan.model.User;
import com.student_loan.model.Item;
import com.student_loan.repository.LoanRepository;
import com.student_loan.repository.UserRepository;
import com.student_loan.repository.ItemRepository;
import com.student_loan.service.LoanService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

class UnitLoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllLoans() {
        Loan loan1 = new Loan(); loan1.setId(1L);
        Loan loan2 = new Loan(); loan2.setId(2L);
        List<Loan> loans = Arrays.asList(loan1, loan2);
        when(loanRepository.findAll()).thenReturn(loans);

        List<Loan> result = loanService.getAllLoans();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testGetLoanById() {
        Loan loan = new Loan(); loan.setId(1L);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        Optional<Loan> result = loanService.getLoanById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetLoansByLender() {
        Loan loan = new Loan(); loan.setId(1L);
        when(loanRepository.findByLender(1L)).thenReturn(Collections.singletonList(loan));

        List<Loan> result = loanService.getLoansByLender(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testGetLoansByBorrower() {
        Loan loan = new Loan(); loan.setId(2L);
        when(loanRepository.findByBorrower(2L)).thenReturn(Collections.singletonList(loan));

        List<Loan> result = loanService.getLoansByBorrower(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void testSaveLoanSuccess() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLender(10L);
        loan.setBorrower(20L);
        loan.setItem(30L);

        User lender = new User(); lender.setId(10L);
        User borrower = new User(); borrower.setId(20L);
        Item item = new Item(); item.setId(30L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(20L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(30L)).thenReturn(Optional.of(item));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan saved = loanService.saveLoan(loan);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
    }

    @Test
    void testSaveLoanLenderNotFound() {
        Loan loan = new Loan(); loan.setId(1L);
        loan.setLender(10L);

        when(userRepository.findById(10L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.saveLoan(loan));
        assertTrue(ex.getMessage().contains("Lender not found with id: 10"));
    }

    @Test
    void testSaveLoanBorrowerNotFound() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLender(10L);
        loan.setBorrower(20L);

        User lender = new User(); lender.setId(10L);
        when(userRepository.findById(10L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(20L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.saveLoan(loan));
        assertTrue(ex.getMessage().contains("Borrower not found with id: 20"));
    }

    @Test
    void testSaveLoanItemNotFound() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLender(10L);
        loan.setBorrower(20L);
        loan.setItem(30L);

        User lender = new User(); lender.setId(10L);
        User borrower = new User(); borrower.setId(20L);
        when(userRepository.findById(10L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(20L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(30L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.saveLoan(loan));
        assertTrue(ex.getMessage().contains("Item not found with id: 30"));
    }

    @Test
    void testCreateLoanSuccess() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLender(10L);
        loan.setBorrower(20L);
        loan.setItem(30L);

        User lender = new User(); lender.setId(10L);
        User borrower = new User(); borrower.setId(20L);
        Item item = new Item(); item.setId(30L);

        when(loanRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(10L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(20L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(30L)).thenReturn(Optional.of(item));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan created = loanService.createLoan(loan);

        assertNotNull(created);
        assertEquals(Loan.Status.IN_USE, created.getLoanStatus());
    }

    @Test
    void testCreateLoanAlreadyExists() {
        Loan loan = new Loan(); loan.setId(1L);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.createLoan(loan));
        assertTrue(ex.getMessage().contains("Loan already exists with id: 1"));
    }

    @Test
    void testDeleteLoan() {
        loanService.deleteLoan(5L);
        verify(loanRepository, times(1)).deleteById(5L);
    }
}