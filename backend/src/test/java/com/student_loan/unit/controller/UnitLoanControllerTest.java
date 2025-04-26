package com.student_loan.unit.controller;

import com.student_loan.controller.LoanController;
import com.student_loan.dtos.LoanRecord;
import com.student_loan.model.Loan;
import com.student_loan.model.User;
import com.student_loan.service.LoanService;
import com.student_loan.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnitLoanControllerTest {

    @Mock
    private LoanService loanService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LoanController loanController;

    private User user;
    private User adminUser;
    private Loan loan;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setAdmin(false);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setEmail("admin@example.com");
        adminUser.setAdmin(true);

        loan = new Loan();
        loan.setId(1L);
        loan.setLender(1L);
        loan.setBorrower(2L);
        loan.setItem(3L);
        loan.setLoanDate(new Date(System.currentTimeMillis()));
        loan.setEstimatedReturnDate(new Date(System.currentTimeMillis() + 86400000)); 
        loan.setLoanStatus(Loan.Status.IN_USE);
    }

    @Test
    public void testGetAllLoans_Success() {
        when(userService.getUserByToken(anyString())).thenReturn(adminUser);
        when(loanService.getAllLoans()).thenReturn(Arrays.asList(loan));

        ResponseEntity<List<Loan>> response = loanController.getAllLoans("token");

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetAllLoans_Unauthorized() {
        when(userService.getUserByToken(anyString())).thenReturn(user);

        ResponseEntity<List<Loan>> response = loanController.getAllLoans("token");

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testGetLoanById_Success_Admin() {
        when(userService.getUserByToken(anyString())).thenReturn(adminUser);
        when(loanService.getLoanById(anyLong())).thenReturn(Optional.of(loan));

        ResponseEntity<Loan> response = loanController.getLoanById(1L, "token");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetLoanById_Success_Owner() {
        when(userService.getUserByToken(anyString())).thenReturn(user);
        when(loanService.getLoanById(anyLong())).thenReturn(Optional.of(loan));

        ResponseEntity<Loan> response = loanController.getLoanById(1L, "token");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetLoanById_Unauthorized() {
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setAdmin(false);

        when(userService.getUserByToken(anyString())).thenReturn(anotherUser);
        when(loanService.getLoanById(anyLong())).thenReturn(Optional.of(loan));

        ResponseEntity<Loan> response = loanController.getLoanById(1L, "token");

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testGetLoansByLender_Success() {
        when(userService.getUserByToken(anyString())).thenReturn(user);
        when(loanService.getLoansByLender(anyLong())).thenReturn(Arrays.asList(loan));

        ResponseEntity<List<Loan>> response = loanController.getLoansByLender("token", 1L);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetLoansByLender_Unauthorized() {
        when(userService.getUserByToken(anyString())).thenReturn(null);

        ResponseEntity<List<Loan>> response = loanController.getLoansByLender("token", 1L);

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testGetLoansByBorrower_Success() {
        when(userService.getUserByToken(anyString())).thenReturn(adminUser);
        when(loanService.getLoansByBorrower(anyLong())).thenReturn(Arrays.asList(loan));

        ResponseEntity<List<Loan>> response = loanController.getLoansByBorrower("token", 2L);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testCreateLoan_Success() {
        when(userService.getUserByToken(anyString())).thenReturn(user);
    
        LoanRecord loanRecord = new LoanRecord(
            null, 
            1L, 
            2L, 
            3L, 
            "2025-01-01", 
            "2025-01-10", 
            null, 
            null, 
            null, 
            "No observations"
        );
    
        ResponseEntity<String> response = loanController.createLoan(loanRecord, "token");
    
        assertEquals(201, response.getStatusCodeValue());
    }
    
    @Test
    public void testUpdateLoan_Success() {
        when(userService.getUserByToken(anyString())).thenReturn(user);
        when(loanService.getLoanById(anyLong())).thenReturn(Optional.of(loan));
    
        LoanRecord loanRecord = new LoanRecord(
            null, 
            1L, 
            2L, 
            3L, 
            "2025-01-01", 
            "2025-01-10", 
            "2025-01-15", 
            "RETURNED", 
            "5", 
            "Updated observations"
        );
    
        ResponseEntity<String> response = loanController.updateLoan(1L, loanRecord, "token");
    
        assertEquals(200, response.getStatusCodeValue());
    }
    


    @Test
public void testUpdateLoan_Unauthorized() {
    when(userService.getUserByToken(anyString())).thenReturn(null);

    LoanRecord loanRecord = new LoanRecord(
        null, 
        2L,   
        3L,   
        4L,   
        "2025-01-01", 
        "2025-01-10", 
        null,  
        "RETURNED",  
        "5",   
        "Updated observations" 
    );

    ResponseEntity<String> response = loanController.updateLoan(1L, loanRecord, "token");

    assertEquals(401, response.getStatusCodeValue());
}


    @Test
    public void testDeleteLoan_Success() {
        when(userService.getUserByToken(anyString())).thenReturn(adminUser);
        when(loanService.getLoanById(anyLong())).thenReturn(Optional.of(loan));

        ResponseEntity<String> response = loanController.deleteLoan(1L, "token");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testDeleteLoan_Unauthorized() {
        when(userService.getUserByToken(anyString())).thenReturn(null);

        ResponseEntity<String> response = loanController.deleteLoan(1L, "token");

        assertEquals(401, response.getStatusCodeValue());
    }
}
