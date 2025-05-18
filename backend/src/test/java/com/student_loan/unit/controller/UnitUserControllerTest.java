package com.student_loan.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import com.student_loan.controller.UserController;
import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.dtos.RegistrationRecord;
import com.student_loan.dtos.UserDTO;
import com.student_loan.dtos.UserRecord;
import com.student_loan.model.User;
import com.student_loan.service.UserService;

class UnitUserControllerTest {

    private UserService userService;
    private UserController userController;
    private static final String ADMIN_TOKEN = "admin-token";
    private static final String USER_TOKEN = "user-token";

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
    @DisplayName("GET /users - Admin retrieves all users successfully")
    void testGetAllUsers_AdminSuccess() {
        User admin = new User();
        admin.setAdmin(true);
        when(userService.getUserByToken(ADMIN_TOKEN)).thenReturn(admin);

        User u1 = new User(); u1.setId(1L);
        User u2 = new User(); u2.setId(2L);
        List<User> all = Arrays.asList(u1, u2);
        when(userService.getAllUsers()).thenReturn(all);

        ResponseEntity<List<User>> response = userController.getAllUsers(ADMIN_TOKEN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(all, response.getBody());
    }

    @Test
    @DisplayName("GET /users - Non-admin valid token returns UNAUTHORIZED and empty list")
    void testGetAllUsers_NonAdminUnauthorized() {
        User nonAdmin = new User();
        nonAdmin.setAdmin(false);
        when(userService.getUserByToken(USER_TOKEN)).thenReturn(nonAdmin);

        ResponseEntity<List<User>> response = userController.getAllUsers(USER_TOKEN);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    @DisplayName("GET /users - Invalid token returns UNAUTHORIZED and empty list")
    void testGetAllUsers_Unauthorized() {
        when(userService.getUserByToken(USER_TOKEN)).thenReturn(null);

        ResponseEntity<List<User>> response = userController.getAllUsers(USER_TOKEN);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    @DisplayName("POST /users/logout - Successful logout returns OK")
    void testLogout_Success() {
        when(userService.logout(USER_TOKEN)).thenReturn(true);

        ResponseEntity<String> response = userController.logout(USER_TOKEN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /users/logout - Invalid token returns UNAUTHORIZED")
    void testLogout_InvalidToken() {
        when(userService.logout(USER_TOKEN)).thenReturn(false);

        ResponseEntity<String> response = userController.logout(USER_TOKEN);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /users/{id} - Admin updates any user successfully")
    void testUpdateUser_AsAdmin() {
        User admin = new User(); admin.setAdmin(true);
        when(userService.getUserByToken(ADMIN_TOKEN)).thenReturn(admin);

        UserRecord record = new UserRecord(
            "María", "García", "maria@dominio.com", "contraseña", "600112233", "Calle Falsa 123", "UNIVERSITY_DEGREE", 4, 1, 4.0, false
        );
        User updated = new User(); updated.setId(5L);
        when(userService.updateUser(any(Long.class), any(User.class))).thenReturn(updated);

        ResponseEntity<User> response = userController.updateUser(5L, record, ADMIN_TOKEN);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    @Test
    @DisplayName("PUT /users/{id} - User updates own data successfully")
    void testUpdateUser_Self() {
        User user = new User(); user.setAdmin(false); user.setId(3L);
        when(userService.getUserByToken(USER_TOKEN)).thenReturn(user);

        UserRecord record = new UserRecord(
            "Luis", "Martínez", "luis@dominio.com", "miClave123", "612334455", "Avenida Siempre Viva 742", "UNIVERSITY_DEGREE", 2, 0, 3.8, false
        );
        User expected = new User(); expected.setId(3L);
        when(userService.updateUser(eq(3L), any(User.class))).thenReturn(expected);

        ResponseEntity<User> response = userController.updateUser(3L, record, USER_TOKEN);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    @DisplayName("PUT /users/{id} - Invalid token returns UNAUTHORIZED without calling service")
    void testUpdateUser_InvalidToken() {
        when(userService.getUserByToken(USER_TOKEN)).thenReturn(null);
        UserRecord record = mock(UserRecord.class);

        ResponseEntity<User> response = userController.updateUser(1L, record, USER_TOKEN);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(userService, never()).updateUser(anyLong(), any());
    }

    @Test
    @DisplayName("PUT /users/{id} - Non-admin updating other user returns UNAUTHORIZED")
    void testUpdateUser_Unauthorized() {
        User user = new User(); user.setAdmin(false); user.setId(3L);
        when(userService.getUserByToken(USER_TOKEN)).thenReturn(user);

        UserRecord record = new UserRecord(
            "Laura", "Sánchez", "laura@dominio.com", "pass123", "620998877", "Plaza Mayor 10", "UNIVERSITY_DEGREE", 1, 0, 4.2, false
        );

        ResponseEntity<User> response = userController.updateUser(4L, record, USER_TOKEN);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /users/{id} - Update non-existing user returns NOT_FOUND")
    void testUpdateUser_NotFound() {
        User admin = new User(); admin.setAdmin(true);
        when(userService.getUserByToken(ADMIN_TOKEN)).thenReturn(admin);

        UserRecord record = new UserRecord(
            "Carlos", "Ramírez", "carlos@dominio.com", "pwd123", "630445566", "Calle del Sol 5", "UNIVERSITY_DEGREE", 3, 2, 2.5, false
        );
        when(userService.updateUser(any(Long.class), any(User.class))).thenThrow(new RuntimeException());

        ResponseEntity<User> response = userController.updateUser(99L, record, ADMIN_TOKEN);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /users/register - Successful registration maps and returns OK")
    void testRegister_Success() {
        RegistrationRecord record = new RegistrationRecord(
            "Pablo", "López", "pablo@dominio.com", "clave123", "600123456", "Calle Luna 5", "UNIVERSITY_DEGREE", 1
        );
        when(userService.register(any(User.class))).thenReturn(true);

        ResponseEntity<String> response = userController.register(record);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered correctly", response.getBody());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).register(captor.capture());
        User saved = captor.getValue();
        assertEquals("Pablo López", saved.getName());
        assertEquals("pablo@dominio.com", saved.getEmail());
        assertEquals(0, saved.getPenalties());
        assertEquals(0.0, saved.getAverageRating());
        assertEquals(User.DegreeType.UNIVERSITY_DEGREE, saved.getDegreeType());
    }

    @Test
    @DisplayName("POST /users/register - Duplicate registration returns BAD_REQUEST")
    void testRegister_Duplicate() {
        when(userService.register(any(User.class))).thenReturn(false);
        RegistrationRecord record = new RegistrationRecord("Ana","Gómez","ana@dom.com","pwd","600000000","Calle", "UNIVERSITY_DEGREE", 2);

        ResponseEntity<String> response = userController.register(record);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The user already exists", response.getBody());
    }

    @Test
    @DisplayName("POST /users/login - Success returns token JSON")
    void testLogin_Success() {
        CredentialsDTO creds = new CredentialsDTO("user@dom.com", "pass");
        when(userService.login(any(CredentialsDTO.class))).thenReturn("tok123");

        ResponseEntity<?> resp = userController.login(creds);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("{\"token\": \"tok123\"}", resp.getBody());
    }

    @Test
    @DisplayName("POST /users/login - Fail returns 401 and message")
    void testLogin_Fail() {
        when(userService.login(any(CredentialsDTO.class))).thenReturn("Invalid credentials");
        CredentialsDTO creds = new CredentialsDTO("user","wrong");

        ResponseEntity<?> resp = userController.login(creds);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertEquals("Invalid credentials", resp.getBody());
    }

    @Test
    @DisplayName("GET /users/{id} - Authorized returns UserDTO")
    void testGetUserById_Success() {
        User auth = new User();
        auth.setEmail("ana@example.com");
        auth.setAdmin(false);
        when(userService.getUserByEmail("ana@example.com")).thenReturn(auth);

        User found = new User();
        found.setId(8L);
        found.setName("Ana Ruiz");
        found.setEmail("a@b.com");
        when(userService.getUserById(8L)).thenReturn(Optional.of(found));

        ResponseEntity<UserDTO> resp = userController.getUserById(8L);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        UserDTO dto = resp.getBody();
        assertEquals(8L, dto.getId());
        assertEquals("Ana Ruiz", dto.getUsername());
        assertEquals("a@b.com", dto.getEmail());
    }

    @Test
    @DisplayName("GET /users/{id} - Unauthorized if no principal")
    void testGetUserById_Unauthorized() {
        when(userService.getUserByEmail("ana@example.com")).thenReturn(null);
        ResponseEntity<UserDTO> resp = userController.getUserById(1L);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /users/{id} - Admin deletes user successfully")
    void testDeleteUser_AsAdmin() {
        User admin = new User(); admin.setAdmin(true);
        when(userService.getUserByToken(ADMIN_TOKEN)).thenReturn(admin);

        ResponseEntity<String> response = userController.deleteUser(10L, ADMIN_TOKEN);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /users/{id}/record - Unauthorized when no authenticated user")
    void testGetUserRecordById_Unauthorized() {
        when(userService.getUserByEmail("ana@example.com")).thenReturn(null);

        ResponseEntity<UserRecord> response = userController.getUserRecordById(123L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

        @Test
    @DisplayName("GET /users/{id}/record - Not Found when target user missing")
    void testGetUserRecordById_NotFound() {
        User requester = new User();
        when(userService.getUserByEmail("ana@example.com")).thenReturn(requester);
        when(userService.getUserById(123L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> userController.getUserRecordById(123L)
        );
        assertEquals("404 NOT_FOUND", ex.getStatusCode().toString());
    }

    @Test
    @DisplayName("GET /users/{id}/record - Success returns full record")
    void testGetUserRecordById_Success() {
        User requester = new User(); requester.setId(1L);
        when(userService.getUserByEmail("ana@example.com")).thenReturn(requester);

        User target = new User();
        target.setId(42L);
        target.setName("Alice Wonderland");
        target.setEmail("alice@wonder.land");
        target.setPassword("secretHash");
        target.setTelephoneNumber("123456789");
        target.setAddress("Rabbit Hole St.");
        target.setDegreeType(User.DegreeType.MASTER);
        target.setDegreeYear(2020);
        target.setPenalties(2);
        target.setAverageRating(4.5);
        target.setAdmin(true);
        when(userService.getUserById(42L)).thenReturn(Optional.of(target));

        ResponseEntity<UserRecord> response = userController.getUserRecordById(42L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserRecord r = response.getBody();
        assertNotNull(r);
        assertEquals("Alice", r.name());
        assertEquals("Wonderland", r.lastName());
        assertEquals("alice@wonder.land", r.email());
        assertEquals("secretHash", r.password());
        assertEquals("123456789", r.telephoneNumber());
        assertEquals("Rabbit Hole St.", r.address());
        assertEquals("MASTER", r.degreeType());
        assertEquals(2020, r.degreeYear());
        assertEquals(2, r.penalties());
        assertEquals(4.5, r.averageRating());
        assertTrue(r.admin());
    }

    @Test
    @DisplayName("userToUserRecord - Full name splits into first and last")
    void testUserToUserRecord_FullName() {
        User u = new User();
        u.setName("Bob Builder");
        u.setEmail("bob@build.it");
        u.setPassword("pw");
        u.setTelephoneNumber("000");
        u.setAddress("Site");
        u.setDegreeType(User.DegreeType.UNIVERSITY_DEGREE);
        u.setDegreeYear(2018);
        u.setPenalties(1);
        u.setAverageRating(3.1);
        u.setAdmin(false);

        UserRecord rec = userController.userToUserRecord(u);

        assertEquals("Bob", rec.name());
        assertEquals("Builder", rec.lastName());
        assertEquals("bob@build.it", rec.email());
        assertEquals("pw", rec.password());
        assertEquals("000", rec.telephoneNumber());
        assertEquals("Site", rec.address());
        assertEquals("UNIVERSITY_DEGREE", rec.degreeType());
        assertEquals(2018, rec.degreeYear());
        assertEquals(1, rec.penalties());
        assertEquals(3.1, rec.averageRating());
        assertFalse(rec.admin());
    }

        @Test
    @DisplayName("userToUserRecord - Single name yields empty lastName")
    void testUserToUserRecord_SingleName() {
        User u = new User();
        u.setName("Madonna");
        u.setEmail("m@ono.name");
        u.setPassword("pw");
        u.setTelephoneNumber("000");
        u.setAddress("Site");
        u.setDegreeType(User.DegreeType.UNIVERSITY_DEGREE);
        u.setDegreeYear(2021);
        u.setPenalties(0);
        u.setAverageRating(5.0);
        u.setAdmin(false);

        UserRecord rec = userController.userToUserRecord(u);

        assertEquals("Madonna", rec.name());
        assertEquals("", rec.lastName());
    }

    @Test
    @DisplayName("DELETE /users/{id} - User deletes own account successfully")
    void testDeleteUser_Self() {
        User user = new User(); user.setAdmin(false); user.setId(7L);
        when(userService.getUserByToken(USER_TOKEN)).thenReturn(user);

        ResponseEntity<String> response = userController.deleteUser(7L, USER_TOKEN);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /users/{id} - Invalid token returns UNAUTHORIZED without calling service")
    void testDeleteUser_InvalidToken() {
        when(userService.getUserByToken(USER_TOKEN)).thenReturn(null);

        ResponseEntity<String> response = userController.deleteUser(1L, USER_TOKEN);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(userService, never()).deleteUser(anyLong());
    }

    @Test
    @DisplayName("DELETE /users/{id} - Unauthorized delete attempt returns UNAUTHORIZED")
    void testDeleteUser_Unauthorized() {
        User user = new User(); user.setAdmin(false); user.setId(7L);
        when(userService.getUserByToken(USER_TOKEN)).thenReturn(user);

        ResponseEntity<String> response = userController.deleteUser(8L, USER_TOKEN);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
