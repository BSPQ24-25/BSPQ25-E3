package com.student_loan.unit.controller;

import com.student_loan.controller.LoanController;
import com.student_loan.dtos.LoanRecord;
import com.student_loan.model.Loan;
import com.student_loan.model.User;
import com.student_loan.service.LoanService;
import com.student_loan.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UnitLoanControllerTest {

    @InjectMocks
    private LoanController loanController;

    @Mock
    private LoanService loanService;

    @Mock
    private UserService userService;

    private User admin;
    private User lender;
    private User borrower;
    private User otherUser;
    private Loan sampleLoan;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        admin = new User(1L, "Admin", "admin@example.com", "pass", "111", "addr", User.DegreeType.UNIVERSITY_DEGREE, 3, 0, 4.5, true);
        lender = new User(2L, "Lender", "lend@example.com", "pass", "222", "addr2", User.DegreeType.UNIVERSITY_DEGREE, 2, 0, 4.0, false);
        borrower = new User(3L, "Borrower", "bor@example.com", "pass", "333", "addr3", User.DegreeType.UNIVERSITY_DEGREE, 2, 0, 3.5, false);
        otherUser = new User(4L, "Other", "oth@example.com", "pass", "444", "addr4", User.DegreeType.UNIVERSITY_DEGREE, 1, 0, 3.0, false);
        sampleLoan = new Loan(10L, lender.getId(), borrower.getId(), 100L,
                Date.valueOf("2025-01-01"), Date.valueOf("2025-02-01"), null,
                Loan.Status.IN_USE, null, "obs");
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /loans - Null user unauthorized")
    void getAllLoans_noUser_unauthorized() {
        when(userService.getUserByToken("token")).thenReturn(null);
        ResponseEntity<List<Loan>> resp = loanController.getAllLoans("token");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertTrue(resp.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /loans - Admin retrieves all loans")
    void getAllLoans_admin_returnsLoans() {
        when(userService.getUserByToken("token")).thenReturn(admin);
        when(loanService.getAllLoans()).thenReturn(Arrays.asList(sampleLoan));
        ResponseEntity<List<Loan>> resp = loanController.getAllLoans("token");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("GET /loans - Unauthorized for non-admin")
    void getAllLoans_nonAdmin_unauthorized() {
        when(userService.getUserByToken("token")).thenReturn(borrower);
        ResponseEntity<List<Loan>> resp = loanController.getAllLoans("token");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertTrue(resp.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /loans/{id} - Admin retrieves loan by ID")
    void getLoanById_admin_success() {
        when(userService.getUserByToken("token")).thenReturn(admin);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));

        ResponseEntity<Loan> resp = loanController.getLoanById(10L, "token");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/{id} - Lender retrieves own loan")
    void getLoanById_lender_success() {
        when(userService.getUserByToken("token")).thenReturn(lender);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));

        ResponseEntity<Loan> resp = loanController.getLoanById(10L, "token");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/{id} - Borrower retrieves own loan")
    void getLoanById_borrower_success() {
        when(userService.getUserByToken("token")).thenReturn(borrower);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));

        ResponseEntity<Loan> resp = loanController.getLoanById(10L, "token");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/{id} - Unauthorized for other user")
    void getLoanById_other_unauthorized() {
        when(userService.getUserByToken("token")).thenReturn(otherUser);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));

        ResponseEntity<Loan> resp = loanController.getLoanById(10L, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/{id} - Not found for missing loan")
    void getLoanById_notFound() {
        when(userService.getUserByToken("token")).thenReturn(admin);
        when(loanService.getLoanById(10L)).thenReturn(Optional.empty());

        ResponseEntity<Loan> resp = loanController.getLoanById(10L, "token");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/{id} - Null user unauthorized")
    void getLoanById_noUser_unauthorized() {
        when(userService.getUserByToken("token")).thenReturn(null);
        ResponseEntity<Loan> resp = loanController.getLoanById(10L, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/lender?lenderId={id} - Lender retrieves own loans")
    void getLoansByLender_self_success() {
        when(userService.getUserByToken("token")).thenReturn(lender);
        when(loanService.getLoansByLender(2L)).thenReturn(Arrays.asList(sampleLoan));

        ResponseEntity<List<Loan>> resp = loanController.getLoansByLender("token", 2L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/lender?lenderId={id} - Admin retrieves any loans")
    void getLoansByLender_admin_success() {
        when(userService.getUserByToken("token")).thenReturn(admin);
        when(loanService.getLoansByLender(2L)).thenReturn(new ArrayList<>());

        ResponseEntity<List<Loan>> resp = loanController.getLoansByLender("token", 2L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/lender?lenderId={id} - Unauthorized for other user")
    void getLoansByLender_unauthorized() {
        when(userService.getUserByToken("token")).thenReturn(borrower);

        ResponseEntity<List<Loan>> resp = loanController.getLoansByLender("token", 2L);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/lender?lenderId={id} - Not found on exception")
    void getLoansByLender_notFoundException() {
        when(userService.getUserByToken("token")).thenReturn(admin);
        when(loanService.getLoansByLender(2L)).thenThrow(new RuntimeException());

        ResponseEntity<List<Loan>> resp = loanController.getLoansByLender("token", 2L);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/lender - Null user unauthorized")
    void getLoansByLender_noUser_unauthorized() {
        when(userService.getUserByToken("token")).thenReturn(null);
        ResponseEntity<List<Loan>> resp = loanController.getLoansByLender("token", 2L);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/borrower?borrowerId={id} - Borrower retrieves own loans")
    void getLoansByBorrower_self_success() {
        when(userService.getUserByToken("token")).thenReturn(borrower);
        when(loanService.getLoansByBorrower(3L)).thenReturn(Arrays.asList(sampleLoan));

        ResponseEntity<List<Loan>> resp = loanController.getLoansByBorrower("token", 3L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/borrower?borrowerId={id} - Admin retrieves any borrower's loans")
    void getLoansByBorrower_admin_success() {
        when(userService.getUserByToken("token")).thenReturn(admin);
        when(loanService.getLoansByBorrower(3L)).thenReturn(new ArrayList<>());

        ResponseEntity<List<Loan>> resp = loanController.getLoansByBorrower("token", 3L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/borrower?borrowerId={id} - Unauthorized for other user")
    void getLoansByBorrower_unauthorized() {
        when(userService.getUserByToken("token")).thenReturn(lender);

        ResponseEntity<List<Loan>> resp = loanController.getLoansByBorrower("token", 3L);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /loans/borrower - Null user unauthorized")
    void getLoansByBorrower_noUser_unauthorized() {
        when(userService.getUserByToken("token")).thenReturn(null);
        ResponseEntity<List<Loan>> resp = loanController.getLoansByBorrower("token", 3L);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("POST /loans - Create loan success")
    void createLoan_success() {
        LoanRecord rec = new LoanRecord(null, lender.getId(), borrower.getId(), 100L,
                "2025-01-01", "2025-02-01", null, null, null, "obs");
        when(userService.getUserByToken("token")).thenReturn(lender);
        when(loanService.saveLoan(any())).thenReturn(sampleLoan);

        ResponseEntity<String> resp = loanController.createLoan(rec, "token");
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    @Test
    @DisplayName("POST /loans - Unauthorized if no user")
    void createLoan_unauthorized() {
        LoanRecord rec = new LoanRecord(null, null, null, null, null, null, null, null, null, null);
        when(userService.getUserByToken("token")).thenReturn(null);

        ResponseEntity<String> resp = loanController.createLoan(rec, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("POST /loans - Bad request on service error")
    void createLoan_badRequest() {
        LoanRecord rec = new LoanRecord(null, lender.getId(), borrower.getId(), 100L,
                "2025-01-01", "2025-02-01", null, null, null, "obs");
        when(userService.getUserByToken("token")).thenReturn(lender);
        doThrow(new RuntimeException("error")).when(loanService).saveLoan(any());

        ResponseEntity<String> resp = loanController.createLoan(rec, "token");
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{id} - Update loan success (lender)")
    void updateLoan_success() {
        LoanRecord rec = new LoanRecord(10L, lender.getId(), borrower.getId(), 100L,
                "2025-01-01", "2025-02-01", "2025-03-01", "RETURNED", "5", "obs2");
        when(userService.getUserByToken("token")).thenReturn(lender);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));
        when(loanService.saveLoan(any())).thenReturn(sampleLoan);

        ResponseEntity<String> resp = loanController.updateLoan(10L, rec, "Bearer token");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{id} - Update loan success (borrower)")
    void updateLoan_borrower_success() {
        LoanRecord rec = new LoanRecord(10L, null, null, null, null, null, null, null, null, null);
        when(userService.getUserByToken("token")).thenReturn(borrower);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));
        when(loanService.saveLoan(any())).thenReturn(sampleLoan);

        ResponseEntity<String> resp = loanController.updateLoan(10L, rec, "Bearer token");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{id} - Not found for missing loan throws exception")
    void updateLoan_notFoundMissing() {
        LoanRecord rec = new LoanRecord(null, null, null, null, null, null, null, null, null, null);
        when(userService.getUserByToken("token")).thenReturn(admin);
        when(loanService.getLoanById(10L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
            loanController.updateLoan(10L, rec, "Bearer token")
        );
    }

    @Test
    @DisplayName("PUT /loans/{id} - Unauthorized on missing header")
    void updateLoan_badHeader() {
        ResponseEntity<String> resp = loanController.updateLoan(10L, null, null);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{id} - Unauthorized for other user")
    void updateLoan_unauthorizedUser() {
        LoanRecord rec = new LoanRecord(null, null, null, null, null, null, null, null, null, null);
        when(userService.getUserByToken("token")).thenReturn(otherUser);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));

        ResponseEntity<String> resp = loanController.updateLoan(10L, rec, "Bearer token");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{id} - Not found on service error")
    void updateLoan_notFoundException() {
        LoanRecord rec = new LoanRecord(null, null, null, null, null, null, null, null, null, null);
        when(userService.getUserByToken("token")).thenReturn(lender);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));
        doThrow(new RuntimeException()).when(loanService).saveLoan(any());

        ResponseEntity<String> resp = loanController.updateLoan(10L, rec, "Bearer token");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{id} - Missing auth header unauthorized")
    void updateLoan_missingHeader_unauthorized() {
        ResponseEntity<String> resp = loanController.updateLoan(10L, new LoanRecord(null,null,null,null,null,null,null,null,null,null), null);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{id} - Invalid auth header unauthorized")
    void updateLoan_invalidHeader_unauthorized() {
        ResponseEntity<String> resp = loanController.updateLoan(10L, new LoanRecord(null,null,null,null,null,null,null,null,null,null), "Invalid token");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{id} - Null user unauthorized")
    void updateLoan_noUser_unauthorized() {
        when(userService.getUserByToken("token")).thenReturn(null);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));

        ResponseEntity<String> resp = loanController.updateLoan(
            10L,
            new LoanRecord(null, null, null, null, null, null, null, null, null, null),
            "Bearer token"
        );
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{id} - Admin updates any loan")
    void updateLoan_admin_success() {
        LoanRecord rec = new LoanRecord(10L, borrower.getId(), lender.getId(), 200L,
                "2025-04-01", "2025-05-01", "2025-06-01", "RETURNED", "4", "adminObs");
        when(userService.getUserByToken("token")).thenReturn(admin);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));
        when(loanService.saveLoan(any())).thenReturn(sampleLoan);

        ResponseEntity<String> resp = loanController.updateLoan(10L, rec, "Bearer token");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{id} - Service error returns message and NOT_FOUND")
    void updateLoan_serviceError_returnsMessage() {
        LoanRecord rec = new LoanRecord(10L, null, null, null, null, null, null, null, null, null);
        when(userService.getUserByToken("token")).thenReturn(lender);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));
        doThrow(new RuntimeException("save failed")).when(loanService).saveLoan(any());

        ResponseEntity<String> resp = loanController.updateLoan(10L, rec, "Bearer token");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals("save failed", resp.getBody());
    }

    @Test
    @DisplayName("PUT /loans/{itemId}/return - Return loan success")
    void returnLoan_success() {
        setSecurityContext(borrower);
        when(loanService.returnLoan(100L, borrower.getId())).thenReturn(true);

        ResponseEntity<Void> resp = loanController.returnLoan(100L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{itemId}/return - Not found when return fails")
    void returnLoan_notFound() {
        setSecurityContext(borrower);
        when(loanService.returnLoan(100L, borrower.getId())).thenReturn(false);

        ResponseEntity<Void> resp = loanController.returnLoan(100L);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{itemId}/return - Internal server error on exception")
    void returnLoan_internalError() {
        setSecurityContext(borrower);
        when(loanService.returnLoan(100L, borrower.getId())).thenThrow(new RuntimeException());

        ResponseEntity<Void> resp = loanController.returnLoan(100L);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /loans/{itemId}/return - Unauthorized if no principal")
    void returnLoan_unauthorized() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("unknown@example.com");

        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        
        SecurityContextHolder.setContext(ctx);
        when(userService.getUserByEmail("unknown@example.com")).thenReturn(null);

        ResponseEntity<Void> resp = loanController.returnLoan(100L);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /loans/{id} - Lender deletes own loan")
    void deleteLoan_lender_success() {
        when(userService.getUserByToken("token")).thenReturn(lender);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));

        ResponseEntity<String> resp = loanController.deleteLoan(10L, "token");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(loanService).deleteLoan(10L);
    }

    @Test
    @DisplayName("DELETE /loans/{id} - Borrower deletes own loan")
    void deleteLoan_borrower_success() {
        when(userService.getUserByToken("token")).thenReturn(borrower);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));

        ResponseEntity<String> resp = loanController.deleteLoan(10L, "token");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(loanService).deleteLoan(10L);
    }

    @Test
    @DisplayName("DELETE /loans/{id} - Admin deletes any loan")
    void deleteLoan_admin_success() {
        when(userService.getUserByToken("token")).thenReturn(admin);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));

        ResponseEntity<String> resp = loanController.deleteLoan(10L, "token");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(loanService).deleteLoan(10L);
    }

    @Test
    @DisplayName("DELETE /loans/{id} - Unauthorized for other user")
    void deleteLoan_unauthorized() {
        when(userService.getUserByToken("token")).thenReturn(otherUser);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));

        ResponseEntity<String> resp = loanController.deleteLoan(10L, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /loans/{id} - Not found on service error")
    void deleteLoan_notFoundException() {
        when(userService.getUserByToken("token")).thenReturn(lender);
        when(loanService.getLoanById(10L)).thenReturn(Optional.of(sampleLoan));
        doThrow(new RuntimeException()).when(loanService).deleteLoan(10L);

        ResponseEntity<String> resp = loanController.deleteLoan(10L, "token");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /loans/{id} - Null user unauthorized")
    void deleteLoan_noUser_unauthorized() {
        when(userService.getUserByToken("token")).thenReturn(null);
        ResponseEntity<String> resp = loanController.deleteLoan(10L, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /loans/{id} - Missing loan throws exception")
    void deleteLoan_missingLoan_exception() {
        when(userService.getUserByToken("token")).thenReturn(admin);
        when(loanService.getLoanById(10L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> loanController.deleteLoan(10L, "token"));
    }

    private void setSecurityContext(User user) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(user.getEmail());
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
    }
}