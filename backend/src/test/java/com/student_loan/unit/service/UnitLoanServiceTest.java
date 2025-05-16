package com.student_loan.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.student_loan.model.Loan;
import com.student_loan.model.User;
import com.student_loan.model.Item;
import com.student_loan.repository.LoanRepository;
import com.student_loan.repository.UserRepository;
import com.student_loan.service.LoanService;
import com.student_loan.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
    void testSaveLoanNewLoanBelowLimit() {
        Loan loan = new Loan();
        loan.setId(null);
        loan.setLender(10L);
        loan.setBorrower(20L);
        loan.setItem(30L);
        loan.setLoanStatus(Loan.Status.IN_USE);

        User lender = new User();   lender.setId(10L);
        User borrower = new User(); borrower.setId(20L);
        Item item = new Item();     item.setId(30L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(20L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(30L)).thenReturn(Optional.of(item));
        when(loanRepository.countByBorrowerAndLoanStatus(20L, Loan.Status.IN_USE)).thenReturn(2);

        Loan saved = new Loan();
        saved.setId(100L);
        when(loanRepository.save(any(Loan.class))).thenReturn(saved);

        Loan result = loanService.saveLoan(loan);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(loanRepository).save(loan);
    }

    @Test
    void testSaveLoanNewLoanAtLimitThrows() {
        Loan loan = new Loan();
        loan.setId(null);
        loan.setLender(10L);
        loan.setBorrower(20L);
        loan.setItem(30L);
        loan.setLoanStatus(Loan.Status.IN_USE);

        User lender = new User();   lender.setId(10L);
        User borrower = new User(); borrower.setId(20L);
        Item item = new Item();     item.setId(30L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(20L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(30L)).thenReturn(Optional.of(item));
        when(loanRepository.countByBorrowerAndLoanStatus(20L, Loan.Status.IN_USE)).thenReturn(3);

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> loanService.saveLoan(loan)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains(
            "Failed to save loan with id null: You already have 3 items reserved"
        ));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void testSaveLoanBorrowerUnderPenaltyThrows() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLender(10L);
        loan.setBorrower(20L);
        loan.setItem(30L);
        loan.setLoanStatus(Loan.Status.IN_USE);

        User lender = new User();
        lender.setId(10L);
        User borrower = mock(User.class);
        when(borrower.getId()).thenReturn(20L);
        when(borrower.hasPenalty()).thenReturn(true);

        when(userRepository.findById(10L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(20L)).thenReturn(Optional.of(borrower));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> loanService.saveLoan(loan)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Cannot borrow items while under penalty.", ex.getReason());

        verify(itemRepository, never()).findById(any());
        verify(loanRepository, never()).save(any());
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
        assertTrue(ex.getMessage().contains("Loan already exists"));
    }

    @Test
    void testCreateLoanLenderNotFound() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLender(10L);
        loan.setBorrower(20L);
        loan.setItem(30L);

        when(loanRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.createLoan(loan));
        assertTrue(ex.getMessage().contains("Lender not found"));
    }

    @Test
    void testCreateLoanBorrowerNotFound() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLender(10L);
        loan.setBorrower(20L);
        loan.setItem(30L);

        User lender = new User(); lender.setId(10L);

        when(loanRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(10L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.createLoan(loan));
        assertTrue(ex.getMessage().contains("Borrower not found"));
    }

    @Test
    void testCreateLoanItemNotFound() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLender(10L);
        loan.setBorrower(20L);
        loan.setItem(30L);

        User lender = new User(); lender.setId(10L);
        User borrower = new User(); borrower.setId(20L);

        when(loanRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(10L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(20L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(30L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.createLoan(loan));
        assertTrue(ex.getMessage().contains("Item not found with id: 30"));
    }

    @Test
    void testGetLentItemsIdByUser() {
        Loan l1 = new Loan(); l1.setId(1L); l1.setLender(100L); l1.setItem(10L); l1.setLoanStatus(Loan.Status.IN_USE);
        Loan l2 = new Loan(); l2.setId(2L); l2.setLender(100L); l2.setItem(20L); l2.setLoanStatus(Loan.Status.IN_USE);
        when(loanRepository.findByLenderAndLoanStatus(100L, Loan.Status.IN_USE))
            .thenReturn(Arrays.asList(l1, l2));

        List<Long> lentIds = loanService.getLentItemsIdByUser(100L);

        assertEquals(2, lentIds.size());
        assertTrue(lentIds.containsAll(Arrays.asList(10L, 20L)));
        verify(loanRepository, times(1)).findByLenderAndLoanStatus(100L, Loan.Status.IN_USE);
    }

    @Test
    void testGetBorrowedItemsIdByUser() {
        Loan b1 = new Loan(); b1.setId(3L); b1.setBorrower(200L); b1.setItem(30L); b1.setLoanStatus(Loan.Status.IN_USE);
        Loan b2 = new Loan(); b2.setId(4L); b2.setBorrower(200L); b2.setItem(40L); b2.setLoanStatus(Loan.Status.IN_USE);
        when(loanRepository.findByBorrowerAndLoanStatus(200L, Loan.Status.IN_USE))
            .thenReturn(Arrays.asList(b1, b2));

        List<Long> borrowedIds = loanService.getBorrowedItemsIdByUser(200L);

        assertEquals(2, borrowedIds.size());
        assertTrue(borrowedIds.containsAll(Arrays.asList(30L, 40L)));
        verify(loanRepository, times(1)).findByBorrowerAndLoanStatus(200L, Loan.Status.IN_USE);
    }

    @Test
    void testReturnLoanSuccess() {
        Long itemId = 1L;
        Long borrowerId = 2L;

        Loan loan = new Loan();
        loan.setId(100L);
        loan.setLoanStatus(Loan.Status.IN_USE);
        loan.setItem(itemId);
        loan.setBorrower(borrowerId);

        when(loanRepository.findByBorrowerAndItemAndLoanStatus(
            borrowerId, itemId, Loan.Status.IN_USE)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = loanService.returnLoan(itemId, borrowerId);

        assertTrue(result);
        assertEquals(Loan.Status.RETURNED, loan.getLoanStatus());
        assertNotNull(loan.getRealReturnDate());
    }

    @Test
    void testReturnLoanNotFound() {
        Long itemId = 1L;
        Long borrowerId = 2L;

        when(loanRepository.findByBorrowerAndItemAndLoanStatus(
            borrowerId, itemId, Loan.Status.IN_USE)).thenReturn(Optional.empty());

        boolean result = loanService.returnLoan(itemId, borrowerId);

        assertFalse(result);
    }

    @Test
    void testReturnLoanSaveThrowsException() {
        Long itemId = 7L;
        Long borrowerId = 8L;
        Loan loan = new Loan();
        loan.setBorrower(borrowerId);
        loan.setItem(itemId);
        loan.setLoanStatus(Loan.Status.IN_USE);

        when(loanRepository.findByBorrowerAndItemAndLoanStatus(borrowerId, itemId, Loan.Status.IN_USE))
            .thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenThrow(new RuntimeException("DB failure"));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> loanService.returnLoan(itemId, borrowerId));
        assertEquals("DB failure", ex.getMessage());
    }

    @Test
    void testDeleteLoan() {
        loanService.deleteLoan(5L);
        verify(loanRepository, times(1)).deleteById(5L);
    }
}