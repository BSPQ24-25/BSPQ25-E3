package com.student_loan.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.student_loan.model.Loan;
import com.student_loan.model.User;
import com.student_loan.model.Item;
import com.student_loan.repository.LoanRepository;
import com.student_loan.repository.UserRepository;
import com.student_loan.service.LoanService;
import com.student_loan.service.NotificationService;
import com.student_loan.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

class UnitLoanServiceTest {
	
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UnitLoanServiceTest.class);

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private LoanService loanService;
    
    @Mock
    private NotificationService notificationService;

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
    @DisplayName("saveLoan - null Optional from userRepository.findById(lender)")
    void testSaveLoanLenderOptNull() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLender(10L);

        when(userRepository.findById(10L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.saveLoan(loan));
        assertTrue(ex.getMessage().contains("Lender not found with id: 10"));
    }

    @Test
    @DisplayName("saveLoan - null Optional from userRepository.findById(borrower)")
    void testSaveLoanBorrowerOptNull() {
        Loan loan = new Loan();
        loan.setId(2L);
        loan.setLender(11L);
        loan.setBorrower(21L);

        User lender = new User(); lender.setId(11L);
        when(userRepository.findById(11L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(21L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.saveLoan(loan));
        assertTrue(ex.getMessage().contains("Borrower not found with id: 21"));
    }

    @Test
    @DisplayName("saveLoan - null Optional from itemRepository.findById")
    void testSaveLoanItemOptNull() {
        Loan loan = new Loan();
        loan.setId(3L);
        loan.setLender(12L);
        loan.setBorrower(22L);
        loan.setItem(32L);

        User lender = new User();   lender.setId(12L);
        User borrower = new User(); borrower.setId(22L);

        when(userRepository.findById(12L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(22L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(32L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.saveLoan(loan));
        assertTrue(ex.getMessage().contains("Item not found with id: 32"));
    }

    
        @Test
    @DisplayName("saveLoan - lenderOpt empty Optional throws RuntimeException")
    void testSaveLoanLenderOptEmpty() {
        Loan loan = new Loan();
        loan.setId(5L);
        loan.setLender(50L);

        when(userRepository.findById(50L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.saveLoan(loan));
        assertTrue(ex.getMessage().contains("Failed to save loan with id 5: Lender not found with id: 50"));
    }

    @Test
    @DisplayName("saveLoan - borrowerOpt empty Optional throws RuntimeException")
    void testSaveLoanBorrowerOptEmpty() {
        Loan loan = new Loan();
        loan.setId(6L);
        loan.setLender(51L);
        loan.setBorrower(61L);

        User lender = new User(); lender.setId(51L);
        when(userRepository.findById(51L)).thenReturn(Optional.of(lender));

        when(userRepository.findById(61L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.saveLoan(loan));
        assertTrue(ex.getMessage().contains("Failed to save loan with id 6: Borrower not found with id: 61"));
    }

    @Test
    @DisplayName("saveLoan - itemOpt empty Optional throws RuntimeException")
    void testSaveLoanItemOptEmpty() {
        Loan loan = new Loan();
        loan.setId(7L);
        loan.setLender(52L);
        loan.setBorrower(62L);
        loan.setItem(72L);

        User lender = new User();   lender.setId(52L);
        User borrower = new User(); borrower.setId(62L);

        when(userRepository.findById(52L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(62L)).thenReturn(Optional.of(borrower));

        when(itemRepository.findById(72L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.saveLoan(loan));
        assertTrue(ex.getMessage().contains("Failed to save loan with id 7: Item not found with id: 72"));
    }

    @Test
    @DisplayName("saveLoan - updating existing loan should skip active loan limit check")
    void testSaveLoanUpdateExistingLoanSkipsLimitCheck() {
        Loan loan = new Loan();
        loan.setId(99L);
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
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        when(loanRepository.countByBorrowerAndLoanStatus(20L, Loan.Status.IN_USE)).thenReturn(5);

        Loan result = loanService.saveLoan(loan);

        assertNotNull(result);
        assertEquals(99L, result.getId());

        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("saveLoan - new loan with status ≠ IN_USE skips active-loan limit check")
    void testSaveLoanNewLoanWithDifferentStatusSkipsLimit() {
        Loan loan = new Loan();
        loan.setId(null);
        loan.setLender(10L);
        loan.setBorrower(20L);
        loan.setItem(30L);
        loan.setLoanStatus(Loan.Status.RETURNED);

        User lender   = new User(); lender.setId(10L);
        User borrower = new User(); borrower.setId(20L);
        Item item     = new Item(); item.setId(30L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(20L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(30L)).thenReturn(Optional.of(item));

        when(loanRepository.countByBorrowerAndLoanStatus(20L, Loan.Status.IN_USE))
            .thenReturn(999);

        Loan saved = new Loan();
        saved.setId(123L);
        when(loanRepository.save(any(Loan.class))).thenReturn(saved);

        Loan result = loanService.saveLoan(loan);

        assertNotNull(result);
        assertEquals(123L, result.getId());

        verify(loanRepository).save(loan);
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
        when(loanRepository.existsById(loan.getId())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.createLoan(loan));
        logger.info(ex.getMessage());
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
    @DisplayName("createLoan - null from findById skips existing check")
    void testCreateLoanFindByIdNull() {
        Loan loan = new Loan();
        loan.setLender(13L);
        loan.setBorrower(23L);
        loan.setItem(33L);

        User lender = new User();   lender.setId(13L);
        User borrower = new User(); borrower.setId(23L);
        Item item = new Item();     item.setId(33L);

        when(loanRepository.findById(null)).thenReturn(null);
        when(userRepository.findById(13L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(23L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(33L)).thenReturn(Optional.of(item));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> {
            Loan l = inv.getArgument(0);
            l.setId(123L);
            return l;
        });

        Loan created = loanService.createLoan(loan);
        assertNotNull(created);
        assertEquals(Loan.Status.IN_USE, created.getLoanStatus());
        assertEquals(123L, created.getId());
    }

        @Test
    @DisplayName("createLoan - findById(id) devuelve Optional.empty() → crea sin excepción")
    void testCreateLoanFindByIdEmptySkipsExistingCheck() {
        Loan loan = new Loan();
        loan.setId(42L);
        loan.setLender(14L);
        loan.setBorrower(24L);
        loan.setItem(34L);

        User lender   = new User(); lender.setId(14L);
        User borrower = new User(); borrower.setId(24L);
        Item item     = new Item(); item.setId(34L);

        when(loanRepository.findById(42L)).thenReturn(Optional.empty());
        when(userRepository.findById(14L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(24L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(34L)).thenReturn(Optional.of(item));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> {
            Loan l = inv.getArgument(0);
            l.setId(4242L);
            return l;
        });

        Loan created = loanService.createLoan(loan);

        assertNotNull(created);
        assertEquals(4242L, created.getId());
        assertEquals(Loan.Status.IN_USE, created.getLoanStatus());
        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("createLoan - findById(id) devuelve null → crea sin excepción")
    void testCreateLoanFindByIdNullSkipsExistingCheck() {
        Loan loan = new Loan();
        loan.setId(null);
        loan.setLender(15L);
        loan.setBorrower(25L);
        loan.setItem(35L);

        User lender   = new User(); lender.setId(15L);
        User borrower = new User(); borrower.setId(25L);
        Item item     = new Item(); item.setId(35L);

        when(loanRepository.findById(null)).thenReturn(null);
        when(userRepository.findById(15L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(25L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(35L)).thenReturn(Optional.of(item));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> {
            Loan l = inv.getArgument(0);
            l.setId(2525L);
            return l;
        });

        Loan created = loanService.createLoan(loan);

        assertNotNull(created);
        assertEquals(2525L, created.getId());
        assertEquals(Loan.Status.IN_USE, created.getLoanStatus());
        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("createLoan - lender findById devuelve null → RuntimeException y sin save()")
    void testCreateLoanLenderFindByIdNull() {
        Loan loan = new Loan();
        loan.setId(7L);
        loan.setLender(17L);
        loan.setBorrower(27L);
        loan.setItem(37L);

        when(loanRepository.findById(7L)).thenReturn(Optional.empty());
        when(userRepository.findById(17L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.createLoan(loan));
        assertTrue(ex.getMessage().contains("Lender not found"));

        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("createLoan - borrower findById devuelve null → RuntimeException y sin save()")
    void testCreateLoanBorrowerFindByIdNull() {
        Loan loan = new Loan();
        loan.setId(8L);
        loan.setLender(18L);
        loan.setBorrower(28L);
        loan.setItem(38L);

        User lender = new User(); lender.setId(18L);

        when(loanRepository.findById(8L)).thenReturn(Optional.empty());
        when(userRepository.findById(18L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(28L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.createLoan(loan));
        logger.info(ex.getMessage());
        assertTrue(ex.getMessage().contains("Borrower not found"));

        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("createLoan - item findById devuelve null → RuntimeException y sin save()")
    void testCreateLoanItemFindByIdNull() {
        Loan loan = new Loan();
        loan.setId(9L);
        loan.setLender(19L);
        loan.setBorrower(29L);
        loan.setItem(39L);

        User lender   = new User(); lender.setId(19L);
        User borrower = new User(); borrower.setId(29L);

        when(loanRepository.findById(9L)).thenReturn(Optional.empty());
        when(userRepository.findById(19L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(29L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(39L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.createLoan(loan));
        assertTrue(ex.getMessage().contains("Item not found with id: 39"));

        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("createLoan - ignora estado preestablecido y lo fuerza a IN_USE")
    void testCreateLoanOverridesProvidedStatus() {
        Loan loan = new Loan();
        loan.setId(null);
        loan.setLender(21L);
        loan.setBorrower(31L);
        loan.setItem(41L);
        loan.setLoanStatus(Loan.Status.RETURNED);

        User lender   = new User(); lender.setId(21L);
        User borrower = new User(); borrower.setId(31L);
        Item item     = new Item(); item.setId(41L);

        when(loanRepository.findById(null)).thenReturn(Optional.empty());
        when(userRepository.findById(21L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(31L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(41L)).thenReturn(Optional.of(item));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan created = loanService.createLoan(loan);

        assertNotNull(created);
        assertEquals(Loan.Status.IN_USE, created.getLoanStatus(), 
                     "Debe forzar siempre el estado IN_USE al crear");
    }

    @Test
    @DisplayName("createLoan - Borrower under penalty → RuntimeException")
    void testCreateLoan_BorrowerUnderPenalty() {
        Loan loan = new Loan();
        loan.setId(null);
        loan.setLender(1L);
        loan.setBorrower(2L);
        loan.setItem(3L);

        User lender   = new User(); lender.setId(1L);
        User borrower = mock(User.class);
        when(borrower.hasPenalty()).thenReturn(true);

        when(loanRepository.findById(null)).thenReturn(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(borrower));

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> loanService.createLoan(loan)
        );
        assertEquals("Cannot borrow items while under penalty.", ex.getMessage());

        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("createLoan - Active loans ≥ 3 for new IN_USE loan → ResponseStatusException")
    void testCreateLoan_ActiveLoansLimitReached() {
        Loan loan = new Loan();
        loan.setId(null);
        loan.setLoanStatus(Loan.Status.IN_USE);
        loan.setLender(1L);
        loan.setBorrower(2L);
        loan.setItem(3L);

        User lender   = new User(); lender.setId(1L);
        User borrower = mock(User.class);
        when(borrower.hasPenalty()).thenReturn(false);

        when(loanRepository.findById(null)).thenReturn(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(3L)).thenReturn(Optional.of(new Item()));

        when(loanRepository.countByBorrowerAndLoanStatus(
            2L, Loan.Status.IN_USE
        )).thenReturn(3);

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> loanService.createLoan(loan)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("You already have 3 items reserved"));
        
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("createLoan - Active loans < 3 for new IN_USE loan → Success")
    void testCreateLoan_ActiveLoansUnderLimit() {
        Loan loan = new Loan();
        loan.setId(null);
        loan.setLoanStatus(Loan.Status.IN_USE);
        loan.setLender(1L);
        loan.setBorrower(2L);
        loan.setItem(3L);

        User lender   = new User(); lender.setId(1L);
        User borrower = mock(User.class);
        when(borrower.hasPenalty()).thenReturn(false);

        Item item = new Item(); item.setId(3L);

        when(loanRepository.findById(null)).thenReturn(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item));

        when(loanRepository.countByBorrowerAndLoanStatus(
            2L, Loan.Status.IN_USE
        )).thenReturn(2);

        when(loanRepository.save(any())).thenAnswer(inv -> {
            Loan saved = inv.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        Loan created = loanService.createLoan(loan);

        assertNotNull(created);
        assertEquals(99L, created.getId());
        assertEquals(Loan.Status.IN_USE, created.getLoanStatus());
        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("createLoan - Non-IN_USE status skips limit check → Success")
    void testCreateLoan_NonInUseSkipsLimit() {
        Loan loan = new Loan();
        loan.setId(null);
        loan.setLoanStatus(Loan.Status.RETURNED);
        loan.setLender(1L);
        loan.setBorrower(2L);
        loan.setItem(3L);

        User lender   = new User(); lender.setId(1L);
        User borrower = mock(User.class);
        when(borrower.hasPenalty()).thenReturn(false);

        Item item = new Item(); item.setId(3L);

        when(loanRepository.findById(null)).thenReturn(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(lender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(borrower));
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item));

        when(loanRepository.save(any())).thenAnswer(inv -> {
            Loan saved = inv.getArgument(0);
            saved.setId(123L);
            return saved;
        });

        Loan created = loanService.createLoan(loan);

        assertNotNull(created);
        assertEquals(123L, created.getId());
        assertEquals(Loan.Status.IN_USE, created.getLoanStatus(),
                    "Debe forzar siempre IN_USE incluso si venía otro estado");
        verify(loanRepository).save(loan);
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

        Item item = new Item();
        item.setId(itemId);

        Loan loan = new Loan();
        loan.setId(100L);
        loan.setLoanStatus(Loan.Status.IN_USE);
        loan.setItem(itemId);
        loan.setBorrower(borrowerId);

        when(loanRepository.findByBorrowerAndItemAndLoanStatus(
            borrowerId, itemId, Loan.Status.IN_USE)).thenReturn(Optional.of(loan));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
        assertEquals("No value present", ex.getMessage());
    }

    @Test
    @DisplayName("returnLoan - null Optional from findByBorrowerAndItemAndLoanStatus")
    void testReturnLoanOptionalNull() {
        Long itemId = 5L, borrowerId = 6L;
        when(loanRepository.findByBorrowerAndItemAndLoanStatus(borrowerId, itemId, Loan.Status.IN_USE))
            .thenReturn(null);

        boolean result = loanService.returnLoan(itemId, borrowerId);
        assertFalse(result);
    }

    @Test
    @DisplayName("returnLoan - Both lender and borrower exist → send two emails")
    void testReturnLoan_NotificationsSent() {
        Long itemId     = 1L;
        Long borrowerId = 2L;
        Long lenderId   = 3L;

        Loan loan = new Loan();
        loan.setLoanStatus(Loan.Status.IN_USE);
        loan.setItem(itemId);
        loan.setBorrower(borrowerId);
        loan.setLender(lenderId);
        when(loanRepository.findByBorrowerAndItemAndLoanStatus(
            borrowerId, itemId, Loan.Status.IN_USE
        )).thenReturn(Optional.of(loan));

        Item item = new Item();
        item.setId(itemId);
        item.setName("Libro de Prueba");
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User lender = new User();
        lender.setEmail("lender@example.com");
        lender.setName("Prestamista Uno");
        when(userRepository.findById(lenderId)).thenReturn(Optional.of(lender));

        User borrower = new User();
        borrower.setEmail("borrower@example.com");
        borrower.setName("Prestatario Dos");
        when(userRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));

        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        boolean result = loanService.returnLoan(itemId, borrowerId);

        assertTrue(result);
        assertEquals(Loan.Status.RETURNED, loan.getLoanStatus());
        assertNotNull(loan.getRealReturnDate());

        ArgumentCaptor<String> toCaptor    = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCap  = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor  = ArgumentCaptor.forClass(String.class);

        verify(notificationService, times(2)).enviarCorreo(
            toCaptor.capture(),
            subjectCap.capture(),
            bodyCaptor.capture()
        );

        List<String> toAddresses = toCaptor.getAllValues();

        assertTrue(toAddresses.contains("lender@example.com"));
        assertTrue(toAddresses.contains("borrower@example.com"));
    }

    @Test
    @DisplayName("returnLoan - Missing lender or borrower → no emails sent")
    void testReturnLoan_MissingUser_NoNotifications() {
        Long itemId     = 1L;
        Long borrowerId = 2L;
        Long lenderId   = 3L;

        Loan loan = new Loan();
        loan.setLoanStatus(Loan.Status.IN_USE);
        loan.setItem(itemId);
        loan.setBorrower(borrowerId);
        loan.setLender(lenderId);
        when(loanRepository.findByBorrowerAndItemAndLoanStatus(
            borrowerId, itemId, Loan.Status.IN_USE
        )).thenReturn(Optional.of(loan));

        Item item = new Item();
        item.setId(itemId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        when(userRepository.findById(lenderId)).thenReturn(Optional.empty());

        User borrower = new User();
        borrower.setEmail("b@b.com");
        borrower.setName("Usuario");
        when(userRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));

        boolean result = loanService.returnLoan(itemId, borrowerId);
        assertTrue(result);

        verify(notificationService, never()).enviarCorreo(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("returnLoan - Lender exists but borrower missing → no emails sent")
    void testReturnLoan_BorrowerMissing_NoNotifications() {
        Long itemId     = 1L;
        Long borrowerId = 2L;
        Long lenderId   = 3L;

        Loan loan = new Loan();
        loan.setLoanStatus(Loan.Status.IN_USE);
        loan.setItem(itemId);
        loan.setBorrower(borrowerId);
        loan.setLender(lenderId);
        when(loanRepository.findByBorrowerAndItemAndLoanStatus(
            borrowerId, itemId, Loan.Status.IN_USE
        )).thenReturn(Optional.of(loan));

        Item item = new Item();
        item.setId(itemId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User lender = new User();
        lender.setEmail("lender@example.com");
        when(userRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(userRepository.findById(borrowerId)).thenReturn(Optional.empty());

        boolean result = loanService.returnLoan(itemId, borrowerId);

        assertTrue(result, "Debe devolver true aunque no envíe correos");
        verify(notificationService, never())
            .enviarCorreo(anyString(), anyString(), anyString());
    }

    @Test
    void testDeleteLoan() {
        loanService.deleteLoan(5L);
        verify(loanRepository, times(1)).deleteById(5L);
    }
}