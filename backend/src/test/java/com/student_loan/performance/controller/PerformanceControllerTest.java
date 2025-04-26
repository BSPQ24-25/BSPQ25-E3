package com.student_loan.performance.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.dtos.RegistrationRecord;
import com.student_loan.dtos.UserDTO;
import com.student_loan.model.Item;
import com.student_loan.model.Loan;
import com.student_loan.model.User;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PerformanceControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String adminToken;
    private Long testUserId;

    @BeforeAll
    public void setup() throws Exception {
        // Register an admin
        RegistrationRecord admin = new RegistrationRecord(
                "AdminTest", "User", "admintest@example.com", "password123");
    
        restTemplate.postForEntity("/users/register", admin, String.class);
    
        // Log in with that admin account
        CredentialsDTO credentials = new CredentialsDTO("admintest@example.com", "password123");
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/users/login", credentials, String.class);
    
        String body = loginResponse.getBody();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(body, new TypeReference<Map<String, String>>() {});
        adminToken = map.get("token");
    }
    


    @Test
    public void testGetAllUsersPerformance() {
        long start = System.nanoTime();
        ResponseEntity<List<User>> response = restTemplate.exchange(
        "/users?token=" + adminToken,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<User>>() {}
        );

        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(durationMs < 500, "Took too long: " + durationMs + "ms");
    }

    @Test
    public void testLogoutPerformance() {
        long start = System.nanoTime();
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/users/logout?token=" + adminToken, null, String.class);
        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(durationMs < 500, "Took too long: " + durationMs + "ms");
    }


    @Test
    public void testGetUserByIdPerformance() {
        long start = System.nanoTime();
        ResponseEntity<UserDTO> response = restTemplate.exchange(
            "/users/{id}?token=" + adminToken, HttpMethod.GET, null, UserDTO.class, 1L);
        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(durationMs < 500, "Took too long: " + durationMs + "ms");
    }




    @Test
    public void testCreateItemPerformance() {
        Map<String, Object> item = new HashMap<>();
        item.put("title", "Test Item");
        item.put("description", "Test Description");
        item.put("type", "BOOK");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(item, headers);

        long start = System.nanoTime();
        ResponseEntity<String> response = restTemplate.postForEntity("/items?token=" + adminToken, request, String.class);
        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(durationMs < 500, "Took too long: " + durationMs + "ms");
    }

    @Test
    public void testCreateLoanPerformance() {
        // Primero crear un item necesario para prestar
        Map<String, Object> item = new HashMap<>();
        item.put("title", "Loan Test Item");
        item.put("description", "Loan Test Description");
        item.put("type", "BOOK");
        HttpEntity<Map<String, Object>> itemRequest = new HttpEntity<>(item);
        restTemplate.postForEntity("/items?token=" + adminToken, itemRequest, String.class);

        // Crear préstamo
        Map<String, Object> loan = new HashMap<>();
        loan.put("lender", 1); // ID del admin logueado (en sistemas reales sería dinámico)
        loan.put("borrower", 1);
        loan.put("item", 1);
        loan.put("loanDate", "2024-04-01");
        loan.put("estimatedReturnDate", "2024-05-01");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> loanRequest = new HttpEntity<>(loan, headers);

        long start = System.nanoTime();
        ResponseEntity<String> response = restTemplate.postForEntity("/loans?token=" + adminToken, loanRequest, String.class);
        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(durationMs < 500, "Took too long: " + durationMs + "ms");
    }

}
