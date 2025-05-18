package com.student_loan.unit;

import com.student_loan.DataInitializer;
import com.student_loan.model.Item;
import com.student_loan.model.Loan;
import com.student_loan.model.User;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.LoanRepository;
import com.student_loan.repository.UserRepository;
import com.student_loan.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.CommandLineRunner;

import java.util.Optional;
import java.util.Collections;
import java.util.List;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private LoanRepository loanRepository;
    
    @Mock
    private NotificationService notificationService;
    
    

    @InjectMocks
    private DataInitializer initializer;

    @BeforeEach
    void setupMocks() {
        // Use lenient stubbing to avoid unnecessary stubbing errors
        lenient().when(userRepository.findByEmail(any(String.class))).thenReturn(null);
        lenient().when(itemRepository.findByName(any(String.class))).thenReturn(null);
        lenient().when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            User u = new User(id, "Test User", "test@example.com", "pass", "phone", "address",
                    User.DegreeType.UNIVERSITY_DEGREE, 1, 0, 4.0, false);
            return Optional.of(u);
        });
        lenient().when(itemRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            Item item = new Item(id, "Item" + id, "desc", "cat", null,
                    id, new Date(), 0.0, null, null);
            return Optional.of(item);
        });
        lenient().when(loanRepository.findByItem(anyLong())).thenReturn(Collections.emptyList());
    }

    @Test
    void testInitData_RunsSuccessfullyAndSavesEntities() {
        CommandLineRunner runner = initializer.initData(userRepository, itemRepository, loanRepository,notificationService);
        assertDoesNotThrow(() -> runner.run());

        // Verify users saved
        verify(userRepository, atLeastOnce()).save(any(User.class));
        // Verify items saved
        verify(itemRepository, atLeastOnce()).save(any(Item.class));
        // Verify loans saved
        verify(loanRepository, atLeastOnce()).save(any(Loan.class));
    }

    @Test
    void testSaveUsers_DoesNotSaveWhenExisting() {
        // When email exists
        User existing = new User(1L, "Existing", "existing@example.com", "", "", "",
                User.DegreeType.MASTER, 1, 0, 4.5, false);
        when(userRepository.findByEmail(eq("existing@example.com"))).thenReturn(existing);

        List<User> list = Collections.singletonList(existing);
        initializer.saveUsers(list, userRepository);
        verify(userRepository, never()).save(existing);
    }

    @Test
    void testSaveItems_DoesNotSaveWhenExisting() {
        Item existing = new Item(1L, "ExistingItem", "desc", "cat", null,
                1L, new Date(), 0.0, null, null);
        when(itemRepository.findByName(eq("ExistingItem"))).thenReturn(existing);

        List<Item> list = Collections.singletonList(existing);
        initializer.saveItems(list, itemRepository);
        verify(itemRepository, never()).save(existing);
    }

    @Test
    void testSaveLoans_DoesNotSaveWhenActiveLoanExists() {
        Loan active = new Loan();
        active.setId(100L);
        active.setLoanStatus(Loan.Status.IN_USE);
        when(loanRepository.findByItem(eq(1L))).thenReturn(Collections.singletonList(active));

        Loan newLoan = new Loan();
        newLoan.setItem(1L);
        initializer.saveLoans(Collections.singletonList(newLoan), loanRepository, notificationService,userRepository, itemRepository);
        verify(loanRepository, never()).save(newLoan);
    }
}