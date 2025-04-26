package com.student_loan.unit.service;

import com.student_loan.model.User;
import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.repository.UserRepository;
import com.student_loan.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.student_loan.service.UserService;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UnitUserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "John Doe", "john.doe@example.com", "encodedPassword",
                "123456789", "123 Main St", User.DegreeType.UNIVERSITY_DEGREE, 1, 0, 4.5, true);
    }

    @Test
    public void testRegister_UserDoesNotExist() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User newUser = new User(user.getId(), user.getName(), user.getEmail(), "password123", user.getTelephoneNumber(),
                user.getAddress(), user.getDegreeType(), user.getDegreeYear(), user.getPenalties(), user.getAverageRating(), user.getAdmin());

        boolean result = userService.register(newUser);

        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegister_UserAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        boolean result = userService.register(user);

        assertFalse(result);
        verify(userRepository, times(0)).save(any());
    }

    @Test
    public void testLogin_UserNotFound() {
        CredentialsDTO credentials = new CredentialsDTO("nonexistent@example.com", "password123");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);
        String result = userService.login(credentials);

        assertEquals("Invalid credentials", result);
    }

    @Test
    public void testUpdateUser_UserExists() {
        User updatedUser = new User(user.getId(), "John Updated", "john.updated@example.com", "newpassword",
                "987654321", "456 New St", User.DegreeType.MASTER, 2, 1, 4.7, false);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword")).thenReturn("$2a$10$encodedPassword");

        userService.updateUser(user.getId(), updatedUser);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("John Updated", savedUser.getName());
        assertEquals("john.updated@example.com", savedUser.getEmail());
        assertEquals("987654321", savedUser.getTelephoneNumber());
        assertEquals("456 New St", savedUser.getAddress());
        assertEquals(User.DegreeType.MASTER, savedUser.getDegreeType());
        assertEquals(2, savedUser.getDegreeYear());
        assertEquals(1, savedUser.getPenalties());
        assertEquals(4.7, savedUser.getAverageRating());
        assertFalse(savedUser.getAdmin());
        assertTrue(savedUser.getPassword().startsWith("$2a$"));
    }

    @Test
    public void testUpdateUser_UserDoesNotExist() {
        User updatedUser = new User(user.getId(), "John Updated", "john.updated@example.com", "newpassword",
                "987654321", "456 New St", User.DegreeType.MASTER, 2, 1, 4.7, false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(user.getId(), updatedUser));
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }
}