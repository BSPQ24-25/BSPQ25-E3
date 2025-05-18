package com.student_loan.performance.service;

import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestActiveConfig;
import com.github.noconnor.junitperf.JUnitPerfReportingConfig;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;
import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.model.Item;
import com.student_loan.model.Loan;
import com.student_loan.model.User;
import com.student_loan.service.ItemService;
import com.student_loan.service.LoanService;
import com.student_loan.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("performance")
@ExtendWith(MockitoExtension.class)
public class PerformanceServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private LoanService loanService;

    private User mockUser;
    private Item mockItem;
    private Loan mockLoan;

    @JUnitPerfTestActiveConfig
    private final static JUnitPerfReportingConfig PERF_CONFIG = JUnitPerfReportingConfig.builder()
            .reportGenerator(new HtmlReportGenerator(System.getProperty("user.dir") + "/target/reports/perf-report.html"))
            .build();

    @BeforeEach
    void setUp() {
        // Mock user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("ana.garcia@email.com");
        mockUser.setPassword("encodedPassword");

        // Mock item
        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("Mock Item");
        mockItem.setOwner(1L);

        // Mock loan
        mockLoan = new Loan();
        mockLoan.setId(1L);
        mockLoan.setLender(1L);
        mockLoan.setBorrower(2L);
        mockLoan.setItem(1L);
        mockLoan.setLoanStatus(Loan.Status.IN_USE);
    }

    // 1. TESTS IN USERSERVICE    

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 5000, warmUpMs = 1000)
    @JUnitPerfTestRequirement(executionsPerSec = 30, percentiles = "95:200ms")
    public void testGetAllUsersPerformance() {
        when(userService.getAllUsers()).thenReturn(List.of(mockUser));
        userService.getAllUsers();
    }

    @Test
    @JUnitPerfTest(threads = 5, durationMs = 5000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testValidLoginPerformance() {
        CredentialsDTO credentials = new CredentialsDTO("ana.garcia@email.com", "dF!94vH*2kQ#bR");
        when(userService.login(credentials)).thenReturn("mockedToken");

        userService.login(credentials);
    }

    @Test
    @JUnitPerfTest(threads = 5, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 5, allowedErrorPercentage = 0.30f)
    public void testInvalidLoginPerformance() {
        CredentialsDTO credentials = new CredentialsDTO("emailnoexiste@email.com", "wrongPassword");
        when(userService.login(credentials)).thenThrow(new RuntimeException("Invalid credentials"));

        Assertions.assertThrows(RuntimeException.class, () -> userService.login(credentials));
    }

    @Test
    @JUnitPerfTest(threads = 3, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testGetUserByInvalidId() {
        when(userService.getUserById(anyLong())).thenThrow(new RuntimeException("User not found"));

        Assertions.assertThrows(RuntimeException.class, () -> userService.getUserById(9999L));
    }

    // 2. TESTS IN ITEMSERVICE

    @Test
    @JUnitPerfTest(threads = 5, durationMs = 4000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testGetAllItemsPerformance() {
        when(itemService.getAllItems()).thenReturn(List.of(mockItem));

        itemService.getAllItems();
    }

    @Test
    @JUnitPerfTest(threads = 5, durationMs = 4000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testGetItemsByUserValidUserPerformance() {
        when(itemService.getItemsByUser(mockUser.getId())).thenReturn(List.of(mockItem));

        itemService.getItemsByUser(mockUser.getId());
    }

    @Test
    @JUnitPerfTest(threads = 3, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testGetItemsByUserInvalidUserPerformance() {
        when(itemService.getItemsByUser(9999L)).thenThrow(new RuntimeException("User not found"));

        Assertions.assertThrows(RuntimeException.class, () -> itemService.getItemsByUser(9999L));
    }

    @Test
    @JUnitPerfTest(threads = 4, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testSaveItemPerformance() {
        when(itemService.saveItem(any(Item.class))).thenReturn(mockItem);

        Item newItem = new Item();
        newItem.setName("Test Item");
        newItem.setOwner(mockUser.getId());

        itemService.saveItem(newItem);
    }

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 2)
    public void testSaveItemInvalidOwnerPerformance() {
        when(itemService.saveItem(any(Item.class))).thenThrow(new RuntimeException("Owner not found"));

        Item newItem = new Item();
        newItem.setName("Invalid Owner Item");
        newItem.setOwner(9999L);

        Assertions.assertThrows(RuntimeException.class, () -> itemService.saveItem(newItem));
    }

    @Test
    @JUnitPerfTest(threads = 3, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 3)
    public void testCreateItemDuplicatePerformance() {
        when(itemService.createItem(any(Item.class))).thenThrow(new RuntimeException("Item already exists"));

        Item existingItem = mockItem;

        Assertions.assertThrows(RuntimeException.class, () -> itemService.createItem(existingItem));
    }

    // 3. TESTS IN LOANSERVICE

    @Test
    @JUnitPerfTest(threads = 5, durationMs = 5000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testGetAllLoansPerformance() {
        when(loanService.getAllLoans()).thenReturn(List.of(mockLoan));

        loanService.getAllLoans();
    }

    @Test
    @JUnitPerfTest(threads = 5, durationMs = 5000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testGetLoansByLenderPerformance() {
        when(loanService.getLoansByLender(mockUser.getId())).thenReturn(List.of(mockLoan));

        loanService.getLoansByLender(mockUser.getId());
    }

    @Test
    @JUnitPerfTest(threads = 5, durationMs = 5000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testGetLoansByLenderInvalidUserPerformance() {
        when(loanService.getLoansByLender(9999L)).thenThrow(new RuntimeException("User not found"));

        Assertions.assertThrows(RuntimeException.class, () -> loanService.getLoansByLender(9999L));
    }

    @Test
    @JUnitPerfTest(threads = 3, durationMs = 4000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testCreateLoanPerformance() {
        when(loanService.createLoan(any(Loan.class))).thenReturn(mockLoan);

        Loan newLoan = new Loan();
        newLoan.setLender(mockUser.getId());
        newLoan.setBorrower(2L);
        newLoan.setItem(mockItem.getId());
        newLoan.setLoanStatus(Loan.Status.IN_USE);

        loanService.createLoan(newLoan);
    }

}
