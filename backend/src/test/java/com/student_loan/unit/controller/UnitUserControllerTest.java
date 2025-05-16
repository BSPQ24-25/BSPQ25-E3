package com.student_loan.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.student_loan.controller.UserController;
import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.dtos.RegistrationRecord;
import com.student_loan.dtos.UserDTO;
import com.student_loan.model.User;
import com.student_loan.service.UserService;

class UnitUserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("ana@example.com");
    }

    @Test
    @DisplayName("POST /users/register - Successful registration")
    void testRegister_Success() {
        RegistrationRecord registrationRecord = new RegistrationRecord(
            "Ana", "Gómez", "ana@example.com", "password", "616238276", "Pancracion Kalea 7", "UNIVERSITY_DEGREE", 3

        );
        when(userService.register(any(User.class))).thenReturn(true);

        ResponseEntity<String> response = userController.register(registrationRecord);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered correctly", response.getBody());
    }

    @Test
    @DisplayName("POST /users/register - User already exists")
    void testRegister_UserAlreadyExists() {
        RegistrationRecord registrationRecord = new RegistrationRecord(
            "Ana", "Gómez", "ana@example.com", "password", "616238276", "Pancracion Kalea 7", "UNIVERSITY_DEGREE", 3

        );
        when(userService.register(any(User.class))).thenReturn(false);

        ResponseEntity<String> response = userController.register(registrationRecord);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The user already exists", response.getBody());
    }

    @Test
    @DisplayName("POST /users/login - Successful login")
    void testLogin_Success() {
        CredentialsDTO credentials = new CredentialsDTO("ana@example.com", "password");
        when(userService.login(any(CredentialsDTO.class))).thenReturn("valid-token");

        ResponseEntity<?> response = userController.login(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"token\": \"valid-token\"}", response.getBody());
    }

    @Test
    @DisplayName("POST /users/login - Invalid credentials")
    void testLogin_InvalidCredentials() {
        CredentialsDTO credentials = new CredentialsDTO("ana@example.com", "wrong-password");
        when(userService.login(any(CredentialsDTO.class))).thenReturn("Invalid credentials");

        ResponseEntity<?> response = userController.login(credentials);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    @DisplayName("GET /users/{id} - Get user details by ID")
    void testGetUserById_Success() {
        User authUser = new User();
        authUser.setEmail("ana@example.com");
        authUser.setAdmin(false);

        User targetUser = new User();
        targetUser.setId(1L);
        targetUser.setName("Carlos Pérez");
        targetUser.setEmail("ana@example.com");

        when(userService.getUserByEmail("ana@example.com")).thenReturn(authUser);
        when(userService.getUserById(1L)).thenReturn(Optional.of(targetUser));

        ResponseEntity<UserDTO> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDTO body = response.getBody();
        assertEquals(1L, body.getId());
        assertEquals("Carlos Pérez", body.getUsername());
        assertEquals("ana@example.com", body.getEmail());
    }

    @Test
    @DisplayName("GET /users/{id} - Unauthorized if no user in context")
    void testGetUserById_Unauthorized() {
        when(userService.getUserByEmail("ana@example.com")).thenReturn(null);

        ResponseEntity<UserDTO> response = userController.getUserById(1L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}