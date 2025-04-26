package com.student_loan.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.dtos.UserDTO;
import com.student_loan.dtos.RegistrationRecord;
import com.student_loan.dtos.UserRecord;
import com.student_loan.model.User;
import com.student_loan.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.student_loan.controller.UserController;

public class UnitUserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    private User adminUser;
    private User normalUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
        
        // Admin user
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setAdmin(true);

        // Normal user
        normalUser = new User();
        normalUser.setId(2L);
        normalUser.setAdmin(false);
    }

    @Test
    public void testGetAllUsers_AdminAccess() {
        when(userService.getUserByToken(anyString())).thenReturn(adminUser);
        when(userService.getAllUsers()).thenReturn(Arrays.asList(adminUser, normalUser));

        ResponseEntity<List<User>> response = userController.getAllUsers("validToken");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testGetAllUsers_UnauthorizedAccess() {
        when(userService.getUserByToken(anyString())).thenReturn(normalUser);

        ResponseEntity<List<User>> response = userController.getAllUsers("invalidToken");

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testLogin_Success() {
        CredentialsDTO credentials = new CredentialsDTO("user@example.com", "password123");
        when(userService.login(any(CredentialsDTO.class))).thenReturn("mockedToken");

        ResponseEntity<?> response = userController.login(credentials);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("mockedToken"));
    }

    @Test
    public void testLogin_InvalidCredentials() {
        CredentialsDTO credentials = new CredentialsDTO("user@example.com", "wrongpassword");
        when(userService.login(any(CredentialsDTO.class))).thenReturn("Invalid credentials");

        ResponseEntity<?> response = userController.login(credentials);

        assertEquals(401, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Invalid credentials"));
    }

    @Test
    public void testLogout_Success() {
        when(userService.logout(anyString())).thenReturn(true);

        ResponseEntity<String> response = userController.logout("validToken");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testLogout_Failure() {
        when(userService.logout(anyString())).thenReturn(false);

        ResponseEntity<String> response = userController.logout("invalidToken");

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testGetUserById_AdminAccess() {
        when(userService.getUserByToken(anyString())).thenReturn(adminUser);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(normalUser));

        ResponseEntity<UserDTO> response = userController.getUserById(2L, "adminToken");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetUserById_UserAccessOwnData() {
        when(userService.getUserByToken(anyString())).thenReturn(normalUser);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(normalUser));

        ResponseEntity<UserDTO> response = userController.getUserById(2L, "userToken");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetUserById_UnauthorizedAccess() {
        when(userService.getUserByToken(anyString())).thenReturn(normalUser);

        ResponseEntity<UserDTO> response = userController.getUserById(3L, "userToken"); // User ID 2 intentando acceder a ID 3

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testUpdateUser_Success_Admin() {
        when(userService.getUserByToken(anyString())).thenReturn(adminUser);
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(adminUser);

        UserRecord userRecord = new UserRecord("John", "Doe", "john@example.com", "password", "123456789", "Address", "UNIVERSITY_DEGREE", 2024, 0, 0.0, true);


        ResponseEntity<User> response = userController.updateUser(1L, userRecord, "adminToken");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testUpdateUser_Unauthorized() {
        when(userService.getUserByToken(anyString())).thenReturn(normalUser);

        UserRecord userRecord = new UserRecord("John", "Doe", "john@example.com", "password", "123456789", "Address", "UNIVERSITY_DEGREE", 2024, 0, 0.0, true);


        ResponseEntity<User> response = userController.updateUser(3L, userRecord, "userToken");

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testRegister_Success() {
        when(userService.register(any(User.class))).thenReturn(true);

        RegistrationRecord registrationRecord = new RegistrationRecord("John", "Doe", "john@example.com", "password123");

        ResponseEntity<String> response = userController.register(registrationRecord);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("User registered correctly"));
    }

    @Test
    public void testRegister_Failure() {
        when(userService.register(any(User.class))).thenReturn(false);

        RegistrationRecord registrationRecord = new RegistrationRecord("John", "Doe", "john@example.com", "password123");

        ResponseEntity<String> response = userController.register(registrationRecord);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("The user already exists"));
    }

    @Test
    public void testDeleteUser_Success_Admin() {
        when(userService.getUserByToken(anyString())).thenReturn(adminUser);

        ResponseEntity<String> response = userController.deleteUser(2L, "adminToken");

        assertEquals(200, response.getStatusCodeValue());
        verify(userService, times(1)).deleteUser(2L);
    }

    @Test
    public void testDeleteUser_Unauthorized() {
        when(userService.getUserByToken(anyString())).thenReturn(normalUser);

        ResponseEntity<String> response = userController.deleteUser(3L, "userToken");

        assertEquals(401, response.getStatusCodeValue());
    }
}
