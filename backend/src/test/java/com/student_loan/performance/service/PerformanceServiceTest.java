package com.student_loan.performance.service;

import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestActiveConfig;
import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.JUnitPerfReportingConfig;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Assertions;


import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.service.ItemService;
import com.student_loan.service.LoanService;
import com.student_loan.service.UserService;
import com.student_loan.model.User;
import com.student_loan.model.Item;
import com.student_loan.model.Loan;


@SpringBootTest
@ExtendWith(JUnitPerfInterceptor.class) //We measure performance with JUnitPerf
public class PerformanceServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private LoanService loanService;

    @JUnitPerfTestActiveConfig //We save the reports made by ContiPerf in target/reports/perf-report.html 
    private final static JUnitPerfReportingConfig PERF_CONFIG = JUnitPerfReportingConfig.builder()
        .reportGenerator(new HtmlReportGenerator(System.getProperty("user.dir") + "/target/reports/perf-report.html"))
        .build();


    // 1. TESTS IN USERSERVICE    
    //Successful test : getAllUsers() Get all the users
    @Test
    @JUnitPerfTest(threads = 10, durationMs = 5000, warmUpMs = 1000)
    @JUnitPerfTestRequirement(executionsPerSec = 30, percentiles = "95:200ms")
    public void testGetAllUsersPerformance() {
        userService.getAllUsers();
    }

    //Successful test : valid login
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 5000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testValidLoginPerformance() {
        CredentialsDTO credentials = new CredentialsDTO("ana.garcia@email.com", "dF!94vH*2kQ#bR");
        userService.login(credentials);
    }

     // Failed test: login with invalid credentials
     @Test
     @JUnitPerfTest(threads = 5, durationMs = 3000)
     @JUnitPerfTestRequirement(executionsPerSec = 5, allowedErrorPercentage = 0.30f)
     public void testInvalidLoginPerformance() {
        CredentialsDTO credentials = new CredentialsDTO("emailnoexiste@email.com", "contrasenamala");
        userService.login(credentials);
     }

    // Failed test: getUserById with invalid ID 
    @Test
    @JUnitPerfTest(threads = 3, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testGetUserByInvalidId() {
        userService.getUserById(9999L);
    }


    // 2. TESTS IN ITEMSERVICE
    // Successful test : getAllItems()
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 4000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testGetAllItemsPerformance() {
        itemService.getAllItems();
    }

    //Successful test: valid login  We test with the first user in our db
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 4000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testGetItemsByUserValidUserPerformance() {
        Long validUserId = userService.getAllUsers().stream()
            .filter(user -> user.getId() != null)
            .findFirst()
            .orElseThrow()
            .getId();

        itemService.getItemsByUser(validUserId);
    }

    // Failed test: getItemsByUser() no user with that id in our db
    @Test
    @JUnitPerfTest(threads = 3, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testGetItemsByUserInvalidUserPerformance() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            itemService.getItemsByUser(9999L); // ID doesn't exist in our db
    });
    }

    //Successfull Test: saveItem(). Save a new item
    @Test
    @JUnitPerfTest(threads = 4, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testSaveItemPerformance() {
        Long validUserId = userService.getAllUsers().stream()
            .filter(user -> user.getId() != null)
            .findFirst()
            .orElseThrow()
            .getId();

        Item newItem = new Item();
        newItem.setName("Test Item");
        newItem.setOwner(validUserId);

        itemService.saveItem(newItem);
}



    // Failed test : saveItem() The user doesn't exist
    @Test
    @JUnitPerfTest(threads = 2, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 2)
    public void testSaveItemInvalidOwnerPerformance() {
        Item newItem = new Item();
        newItem.setName("Invalid Owner Item");
        newItem.setOwner(9999L); // Non existing user

        Assertions.assertThrows(RuntimeException.class, () -> {
            itemService.saveItem(newItem);
        });
    }

    // Failed test: createItem() the item already exists
    @Test
    @JUnitPerfTest(threads = 3, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 3)
    public void testCreateItemDuplicatePerformance() {
        Item existingItem = itemService.getAllItems().stream()
            .filter(i -> i.getId() != null)
            .findFirst()
            .orElseThrow();

        Assertions.assertThrows(RuntimeException.class, () -> {
            itemService.createItem(existingItem);
        });
    }


    // 3. TEST IN LOAN SERVICE

    //Successful test: get all the loans
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 5000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testGetAllLoansPerformance() {
        loanService.getAllLoans();
    }


    //Successful test: get the loans of a user
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 5000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testGetLoansByLenderPerformance() {
        Long lenderId = userService.getAllUsers().get(0).getId(); 
        loanService.getLoansByLender(lenderId);
    }

    // Failed test: the user whose loans we want to get doesn't exist
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 5000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testGetLoansByLenderInvalidUserPerformance() {
        try {
            loanService.getLoansByLender(9999L);  // Usuario no existente
        } catch (RuntimeException e) {
        }
    }

    // Successful test: create a loan
    @Test
    @JUnitPerfTest(threads = 3, durationMs = 4000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testCreateLoanPerformance() {
        Loan newLoan = new Loan();
        newLoan.setLender(userService.getAllUsers().get(0).getId());
        newLoan.setBorrower(userService.getAllUsers().get(1).getId());
        newLoan.setItem(itemService.getAllItems().get(0).getId());
        newLoan.setLoanStatus(Loan.Status.IN_USE);

        loanService.createLoan(newLoan);
    }

    
}
