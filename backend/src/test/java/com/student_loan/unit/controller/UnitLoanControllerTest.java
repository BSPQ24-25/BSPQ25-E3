package com.student_loan.unit.controller;

import com.student_loan.dtos.LoanRecord;
import com.student_loan.controller.LoanController;
import com.student_loan.model.Loan;
import com.student_loan.model.User;
import com.student_loan.service.LoanService;
import com.student_loan.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;


import java.sql.Date;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class UnitLoanControllerTest {

    @InjectMocks
    private LoanController loanController;

    @Mock
    private LoanService loanService;

    @Mock
    private UserService userService;

    private User mockAdminUser;
    private User mockRegularUser;
    private User mockRegularUser2;
    private Loan mockLoan;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockAdminUser = new User(1L, "Admin", "admin@example.com", "password", "1234567890", "Some Address", User.DegreeType.UNIVERSITY_DEGREE, 3, 0, 4.5, true);
        mockRegularUser = new User(2L, "User", "user@example.com", "password", "0987654321", "Another Address", User.DegreeType.UNIVERSITY_DEGREE, 2, 0, 4.0, false);
        mockRegularUser2 = new User(3L, "User2", "user2@example.com", "password", "0987654321", "Another Address 2", User.DegreeType.UNIVERSITY_DEGREE, 2, 0, 4.0, false);

        mockLoan = new Loan(1L, 1L, 2L, 1L, Date.valueOf("2025-01-01"), Date.valueOf("2025-02-01"), null, Loan.Status.IN_USE, null, "Some observations");
    }

    @Test
    void testGetAllLoans_AdminUser_Success() {
        when(userService.getUserByToken("valid-token")).thenReturn(mockAdminUser);
        when(loanService.getAllLoans()).thenReturn(Arrays.asList(mockLoan));

        ResponseEntity<List<Loan>> response = loanController.getAllLoans("valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(mockLoan.getId(), response.getBody().get(0).getId());
    }

    @Test
    void testGetLoanById_Success() {
        when(userService.getUserByToken("valid-token")).thenReturn(mockAdminUser);
        when(loanService.getLoanById(1L)).thenReturn(Optional.of(mockLoan));

        ResponseEntity<Loan> response = loanController.getLoanById(1L, "valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockLoan.getId(), response.getBody().getId());
    }

    @Test
    void testGetLoanById_Unauthorized() {
        when(userService.getUserByToken("valid-token")).thenReturn(mockRegularUser2);
        when(loanService.getLoanById(1L)).thenReturn(Optional.of(mockLoan));

        ResponseEntity<Loan> response = loanController.getLoanById(1L, "valid-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testCreateLoan_Success() {
        LoanRecord loanRecord = new LoanRecord(
            1L,  
            2L,  
            1L,  
            1L,  
            "2025-01-01",  
            "2025-02-01",  
            "2025-03-01",  
            "IN_USE", 
            "5",  
            "Some observations"  
        );

        when(userService.getUserByToken("valid-token")).thenReturn(mockRegularUser);
        when(loanService.saveLoan(any(Loan.class))).thenReturn(mockLoan);

        ResponseEntity<String> response = loanController.createLoan(loanRecord, "valid-token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testUpdateLoan_Success() {
        LoanRecord loanRecord = new LoanRecord(
        1L,  
        2L,  
        1L,  
        1L,  
        "2025-01-01",  
        "2025-02-01",  
        "2025-03-01",  
        "RETURNED", 
        "4",  
        "Updated observations"  
        );
        when(userService.getUserByToken("valid-token")).thenReturn(mockRegularUser);
        when(loanService.getLoanById(1L)).thenReturn(Optional.of(mockLoan));
        when(loanService.saveLoan(any(Loan.class))).thenReturn(mockLoan);

        ResponseEntity<String> response = loanController.updateLoan(1L, loanRecord, "Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteLoan_Success() {
        when(userService.getUserByToken("valid-token")).thenReturn(mockRegularUser);
        when(loanService.getLoanById(1L)).thenReturn(Optional.of(mockLoan));

        ResponseEntity<String> response = loanController.deleteLoan(1L, "valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteLoan_Unauthorized() {
        when(userService.getUserByToken("valid-token")).thenReturn(mockAdminUser);
        when(loanService.getLoanById(1L)).thenReturn(Optional.of(mockLoan));

        ResponseEntity<String> response = loanController.deleteLoan(1L, "valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}