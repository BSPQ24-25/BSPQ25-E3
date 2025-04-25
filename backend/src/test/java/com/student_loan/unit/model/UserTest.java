package com.student_loan.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.student_loan.model.User;
import com.student_loan.repository.UserRepository;

class UserTest {

    private UserRepository repository;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test", "test@example.com", "pass123", "645 890 876", 
                "123 Main St", User.DegreeType.UNIVERSITY_DEGREE, 2022, 0, 4.2, false);

        repository = mock(UserRepository.class);
    }

    @Test
    @DisplayName("Should Add User Successfully")
    void testAddUser() {
        when(repository.save(user)).thenReturn(user);

        User result = repository.save(user);

        verify(repository, times(1)).save(user);

        assertEquals("test@example.com", result.getEmail());
        assertEquals(User.DegreeType.UNIVERSITY_DEGREE, result.getDegreeType());
    }

    @Test
    @DisplayName("Should Retrieve User By ID")
    void testGetUserById() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        User result = repository.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));

        assertNotNull(result);
        assertEquals("Test", result.getName());
    }

    @Test
    @DisplayName("Should Update User Email")
    void testUpdateUserEmail() {
        user.setEmail("test.new@example.com");

        when(repository.save(user)).thenReturn(user);
        User result = repository.save(user);

        verify(repository, times(1)).save(user);
        assertEquals("test.new@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Should Throw Exception When User Not Found")
    void testGetUserById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            repository.findById(999L)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should Get and Set Degree Type Correctly")
    void testGetSetDegreeType() {
        user.setDegreeType(User.DegreeType.MASTER);
        assertEquals(User.DegreeType.MASTER, user.getDegreeType());
    }

    @Test
    @DisplayName("Should Get and Set Admin Flag Correctly")
    void testGetSetAdmin() {
        user.setAdmin(true);
        assertEquals(true, user.getAdmin());
    }

    @Test
    @DisplayName("Should Delete User Successfully")
    void testDeleteUser() {
        doNothing().when(repository).delete(user);

        repository.delete(user);

        verify(repository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Should Update User Name")
    void testUpdateUserName() {
        user.setName("New Name");

        when(repository.save(user)).thenReturn(user);
        User result = repository.save(user);

        verify(repository, times(1)).save(user);
        assertEquals("New Name", result.getName());
    }

    @Test
    @DisplayName("Should Get and Set Password Correctly")
    void testGetSetPassword() {
        user.setPassword("newpassword123");
        assertEquals("newpassword123", user.getPassword());
    }

    @Test
    @DisplayName("Should Update User Phone Number")
    void testUpdateUserPhoneNumber() {
        user.setTelephoneNumber("123 456 789");

        when(repository.save(user)).thenReturn(user);
        User result = repository.save(user);

        verify(repository, times(1)).save(user);
        assertEquals("123 456 789", result.getTelephoneNumber());
    }
}