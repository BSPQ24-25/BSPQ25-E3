package com.student_loan.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.student_loan.controller.UserController;
import com.student_loan.model.User;
import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.dtos.RegistrationRecord;
import com.student_loan.dtos.UserDTO;
import com.student_loan.service.UserService;

class UnitUserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);

        // Configurar el contexto de seguridad
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    @DisplayName("POST /users/register - Successful registration")
    void testRegister_Success() {
        // Crear un objeto RegistrationRecord con datos válidos
        RegistrationRecord registrationRecord = new RegistrationRecord(
            "John", "Doe", "john@example.com", "password"
        );

        // Simulando el comportamiento del servicio para registrar un usuario
        when(userService.register(any(User.class))).thenReturn(true);

        // Simulando el controlador
        ResponseEntity<String> response = userController.register(registrationRecord);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered correctly", response.getBody());
    }

    @Test
    @DisplayName("POST /users/register - User already exists")
    void testRegister_UserAlreadyExists() {
        // Crear un objeto RegistrationRecord con datos válidos
        RegistrationRecord registrationRecord = new RegistrationRecord(
            "John", "Doe", "john@example.com", "password"
        );

        // Simulando un usuario ya existente
        when(userService.register(any(User.class))).thenReturn(false);

        // Simulando el controlador
        ResponseEntity<String> response = userController.register(registrationRecord);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The user already exists", response.getBody());
    }

    @Test
    @DisplayName("POST /users/login - Successful login")
    void testLogin_Success() {
        CredentialsDTO credentials = new CredentialsDTO("john@example.com", "password");
        when(userService.login(any(CredentialsDTO.class))).thenReturn("valid-token");

    
        ResponseEntity<?> response = userController.login(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"token\": \"valid-token\"}", response.getBody());
    }

    @Test
    @DisplayName("POST /users/login - Invalid credentials")
    void testLogin_InvalidCredentials() {
        CredentialsDTO credentials = new CredentialsDTO("john@example.com", "wrong-password");
        when(userService.login(any(CredentialsDTO.class))).thenReturn("Invalid credentials");

    
        ResponseEntity<?> response = userController.login(credentials);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

   // TODO We have to change these two tests because they are not doing the real thing
    @Test
    @DisplayName("GET /users/{id} - Get user details")
    void testGetUserById_Success() {
        // Mock the User object
        User mockUser = new User(1L, "John", "john@example.com", "password", "1234567890", "Some Address", User.DegreeType.UNIVERSITY_DEGREE, 3, 0, 4.5, true);
    
        // Mock the service methods
        when(userService.getUserById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(userService.getUserByToken("valid-token")).thenReturn(mockUser);

        // Call the controller method
        ResponseEntity<UserDTO> response = userController.getUserById2(1L, "valid-token");

        // Validate the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John", response.getBody().getUsername()); // Corrected method name
        assertEquals("john@example.com", response.getBody().getEmail()); // Corrected method name
    }

    @Test
    @DisplayName("GET /users/{id} - Unauthorized access")
    void testGetUserById_Unauthorized() {
        when(userService.getUserByToken("invalid-token")).thenReturn(null);

        ResponseEntity<UserDTO> response = userController.getUserById2(1L, "invalid-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
