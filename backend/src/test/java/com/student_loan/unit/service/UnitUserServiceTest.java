package com.student_loan.unit.service;

import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.model.User;
import com.student_loan.model.User.DegreeType;
import com.student_loan.repository.UserRepository;
import com.student_loan.security.JwtUtil;
import com.student_loan.service.NotificationService;
import com.student_loan.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    
    @Mock
    private NotificationService notificationService;

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        user1 = new User(1L, "Maria Lopez", "maria@example.com", "encodedPass", "+341234567", "Madrid",
                          DegreeType.UNIVERSITY_DEGREE, 2, 0, 4.5, false);
        user2 = new User(2L, "Luis Perez", "luis@example.com", "encodedPass2", "+341234568", "Barcelona",
                          DegreeType.MASTER, 3, 1, 4.8, true);

        Field tokensField = UserService.class.getDeclaredField("tokens");
        tokensField.setAccessible(true);
        ((Map<?,?>)tokensField.get(userService)).clear();
    }

    @Test
    public void testGetAllUsersReturnsList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        List<User> list = userService.getAllUsers();
        assertEquals(2, list.size());
        assertTrue(list.contains(user1) && list.contains(user2));
    }

    @Test
    public void testGetUserByIdFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Optional<User> out = userService.getUserById(1L);
        assertTrue(out.isPresent());
        assertEquals("Maria Lopez", out.get().getName());
    }

    @Test
    public void testGetUserByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<User> out = userService.getUserById(99L);
        assertFalse(out.isPresent());
    }

    @Test
    public void testRegister_UserDoesNotExist() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(null);
        when(passwordEncoder.encode("securePass123")).thenReturn("encodedPassNew");

        User newUser = new User(null, "Pepe", user1.getEmail(), "securePass123", "+341234569", "Valencia",
                                DegreeType.MASTER, 1, 0, 5.0, false);

        boolean res = userService.register(newUser);
        assertTrue(res);

        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(cap.capture());
        assertEquals("encodedPassNew", cap.getValue().getPassword());
    }

    @Test
    public void testRegister_UserAlreadyExists() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
        boolean res = userService.register(user1);
        assertFalse(res);
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testLogin_UserExists() {
        CredentialsDTO cred = new CredentialsDTO(user1.getEmail(), "plain123");
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
        when(passwordEncoder.matches("plain123", user1.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user1.getEmail())).thenReturn("tokenXYZ");

        String out = userService.login(cred);
        assertEquals("tokenXYZ", out);
    }

    @Test
    public void testLogin_UserNotFound() {
        CredentialsDTO cred = new CredentialsDTO("no@no.com", "x");
        when(userRepository.findByEmail("no@no.com")).thenReturn(null);
        String out = userService.login(cred);
        assertEquals("Invalid credentials", out);
    }

    @Test
    public void testLogin_UserAlreadyLoggedIn() throws Exception {
        CredentialsDTO cred = new CredentialsDTO(user1.getEmail(), "x");
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);

        Field tokensField = UserService.class.getDeclaredField("tokens");
        tokensField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, User> tokens = (Map<String, User>) tokensField.get(userService);
        tokens.put("existingToken", user1);

        String out = userService.login(cred);
        assertEquals("User already logged in", out);
    }

    @Test
    public void testLogin_InvalidPassword() {
        CredentialsDTO cred = new CredentialsDTO(user1.getEmail(), "wrongPass");
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(user1);
        when(passwordEncoder.matches("wrongPass", user1.getPassword())).thenReturn(false);

        String out = userService.login(cred);
        assertEquals("Invalid credentials", out);
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    public void testLogin_StoresTokenOnSuccess() throws Exception {
        CredentialsDTO cred = new CredentialsDTO(user2.getEmail(), "plain456");
        when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
        when(passwordEncoder.matches("plain456", user2.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user2.getEmail())).thenReturn("newToken");

        String out = userService.login(cred);
        assertEquals("newToken", out);

        Field tokensField = UserService.class.getDeclaredField("tokens");
        tokensField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, User> tokens = (Map<String, User>) tokensField.get(userService);
        assertTrue(tokens.containsKey("newToken"));
        assertSame(user2, tokens.get("newToken"));
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        Field tokensField = UserService.class.getDeclaredField("tokens");
        tokensField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, User> tokens = (Map<String, User>) tokensField.get(userService);
        tokens.put("tkn", user2);

        assertTrue(userService.logout("tkn"));
        assertFalse(tokens.containsKey("tkn"));
    }

    @Test
    public void testLogoutFailure() {
        assertFalse(userService.logout("nope"));
    }

    @Test
    public void testUpdateUser_UserExistsFull() {
        User updated = new User(1L, "Ana", "ana@new.com", "newPass", "+341111111", "Sevilla",
                                DegreeType.DOCTORATE, 4, 2, 4.2, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User out = userService.updateUser(1L, updated);
        assertEquals("Ana", out.getName());
        assertEquals("ana@new.com", out.getEmail());
        assertEquals("+341111111", out.getTelephoneNumber());
        assertEquals("Sevilla", out.getAddress());
        assertEquals(DegreeType.DOCTORATE, out.getDegreeType());
        assertEquals(4, out.getDegreeYear());
        assertEquals(2, out.getPenalties());
        assertEquals(4.2, out.getAverageRating());
        assertTrue(out.getAdmin());
        assertEquals("encodedNewPass", out.getPassword());
    }

    @Test
    public void testUpdateUser_UserExistsPartial() {
        User partial = new User();
        partial.setEmail("partial@new.com");
        partial.setPassword("");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User out = userService.updateUser(1L, partial);
        assertEquals("Maria Lopez", out.getName());
        assertEquals("partial@new.com", out.getEmail());
        assertEquals(user1.getPassword(), out.getPassword());
    }

    @Test
    public void testUpdateUser_PasswordNull_KeepsOldPassword() {
        User partial = new User();
        partial.setName("Nuevo Nombre");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User out = userService.updateUser(1L, partial);

        assertEquals("Nuevo Nombre", out.getName());
        assertEquals(user1.getPassword(), out.getPassword(), "La contraseña debe mantenerse igual");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    public void testUpdateUser_UserDoesNotExist() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.updateUser(5L, new User()));
    }

    @Test
    public void testDeleteUser_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        userService.deleteUser(2L);
        verify(userRepository).deleteById(2L);
    }

    @Test
    public void testDeleteUser_NotFound() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUser(3L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    public void testGetUserByToken() throws Exception {
        Field tokensField = UserService.class.getDeclaredField("tokens");
        tokensField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, User> tokens = (Map<String, User>) tokensField.get(userService);
        tokens.put("abc", user2);

        assertSame(user2, userService.getUserByToken("abc"));
    }

    @Test
    public void testGetUserByEmail() {
        when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
        assertSame(user2, userService.getUserByEmail(user2.getEmail()));
    }
}