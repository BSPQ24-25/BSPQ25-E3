package com.student_loan.integration;

import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.model.Item;
import com.student_loan.model.Loan;
import com.student_loan.model.User;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.UserRepository;
import com.student_loan.repository.LoanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentLoanIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private LoanRepository loanRepository;


    @Test
    void testEndToEndLoanFlow() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 0. Delete those users if they exist 
        User lenderDelete = userRepository.findByEmail("unai.gonzalez@example.com");
        if (lenderDelete != null) {
            userRepository.deleteById(lenderDelete.getId());
        }

        User borrowerDelete = userRepository.findByEmail("amaia.lopez@example.com");
        if (borrowerDelete != null) {
            userRepository.deleteById(borrowerDelete.getId());
        }

        // 1. Register lender and borrower
        Map<String,String> lenderReg = Map.of(
            "name","Unai","lastName","Gonzalez","email","unai.gonzalez@example.com","password","UnaiPass1!"
        );
        ResponseEntity<String> regL = restTemplate.postForEntity("/users/register", lenderReg, String.class);
        assertEquals(HttpStatus.OK, regL.getStatusCode());

        Map<String,String> borrowerReg = Map.of(
            "name","Amaia","lastName","Lopez","email","amaia.lopez@example.com","password","AmaiaPass2!"
        );
        ResponseEntity<String> regB = restTemplate.postForEntity("/users/register", borrowerReg, String.class);
        assertEquals(HttpStatus.OK, regB.getStatusCode());

        // 2. Login both
        CredentialsDTO credsL = new CredentialsDTO("unai.gonzalez@example.com","UnaiPass1!");
        ResponseEntity<String> loginLResponse = restTemplate.postForEntity("/users/login", credsL, String.class);

        assertEquals(HttpStatus.OK, loginLResponse.getStatusCode());
        Map<String,String> loginLMap = mapper.readValue(loginLResponse.getBody(), Map.class);

        String tokenL = loginLMap.get("token");
        assertNotNull(tokenL);

        CredentialsDTO credsB = new CredentialsDTO("amaia.lopez@example.com","AmaiaPass2!");
        ResponseEntity<String> loginBResponse = restTemplate.postForEntity("/users/login", credsB, String.class);

        assertEquals(HttpStatus.OK, loginBResponse.getStatusCode());
        Map<String,String> loginBMap = mapper.readValue(loginBResponse.getBody(), Map.class);

        String tokenB = loginBMap.get("token");
        assertNotNull(tokenB);

        // 3. Fetch user IDs
        User lender = userRepository.findByEmail("unai.gonzalez@example.com");
        User borrower = userRepository.findByEmail("amaia.lopez@example.com");

        assertNotNull(lender);
        assertNotNull(borrower);

        Long lenderId = lender.getId();
        Long borrowerId = borrower.getId();

        // 4. Create item as lender
        Item itemDelete = itemRepository.findByName("Test SmartWatch");
        if (itemDelete != null) {
            itemRepository.deleteById(itemDelete.getId());
        }

        Map<String, Object> itemReq = Map.of(
            "name", "Test SmartWatch",
            "description", "Dell XPS 13",
            "category", "Electronics",
            "status", "AVAILABLE",
            "condition", "LIKE_NEW",
            "imageUrl", "http://example.com/xps.jpg"
        );

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
            "/items?token=" + tokenL,
            itemReq,
            String.class
        );

        Item fetchedItem = itemRepository.findByName("Test SmartWatch");
        assertNotNull(fetchedItem);
        Long itemId = fetchedItem.getId();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Test SmartWatch", fetchedItem.getName());

        // 5. Create loan as borrower
        Map<String,Object> loanReq = Map.of(
            "lender", lenderId,
            "borrower", borrowerId,
            "item", itemId,
            "loanDate", LocalDate.now().toString(),
            "estimatedReturnDate", LocalDate.now().plusDays(7).toString(),
            "observations", "Test loan"
        );

        ResponseEntity<String> cLoan = restTemplate.postForEntity(
            "/loans?token=" + tokenB, loanReq, String.class);

        assertEquals(HttpStatus.CREATED, cLoan.getStatusCode());

        // 6. Verify loan via GET loans/borrower
        ResponseEntity<Loan[]> respLoans = restTemplate.getForEntity(
            "/loans/borrower?token=" + tokenB + "&borrowerId=" + borrowerId, Loan[].class);
        
        assertEquals(HttpStatus.OK, respLoans.getStatusCode());
        assertNotNull(respLoans.getBody());
        
        Loan[] loans = respLoans.getBody();
        assertTrue(loans.length > 0);
        
        Long loanId = loans[0].getId();
        assertEquals(Loan.Status.IN_USE, loans[0].getLoanStatus());
        assertEquals(loanId, loans[0].getId());
        System.out.println("INTEGRATION TEST FINISHED");

        // // 7. Return loan
        // Map<String, String> returnLoanRequest = Map.of(
        //     "realReturnDate", LocalDate.now().plusDays(5).toString(),
        //     "loanStatus", Loan.Status.RETURNED.toString(),
        //     "rating", "4.5",
        //     "observations", "Good condition"
        // );

        // HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_JSON);
        // headers.setBearerAuth(tokenB);

        // HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(returnLoanRequest, headers);

        // ResponseEntity<String> returnLoanResponse = restTemplate.exchange(
        //     "/loans/" + loanId,
        //     HttpMethod.PUT,
        //     requestEntity,
        //     String.class
        // );

        // System.out.println("HOLA" + returnLoanResponse.getBody());
        // assertEquals(HttpStatus.OK, returnLoanResponse.getStatusCode());

        // // Step 8: Verify loan status is updated
        // Optional<Loan> updatedLoanOpt = loanRepository.findById(loanId);
        // assertTrue(updatedLoanOpt.isPresent());

        // Loan updatedLoan = updatedLoanOpt.get();
        // assertEquals(Loan.Status.RETURNED, updatedLoan.getLoanStatus());

        // // Step 9: Cleanup created loan and item
        // ResponseEntity<Void> deleteLoanResponse = restTemplate.exchange(
        //     "/loans/" + loanId + "?token=" + tokenB,
        //     HttpMethod.DELETE,
        //     null,
        //     Void.class
        // );
        // assertEquals(HttpStatus.NO_CONTENT, deleteLoanResponse.getStatusCode());

        // ResponseEntity<Void> deleteItemResponse = restTemplate.exchange(
        //     "/items/" + itemId + "?token=" + tokenL,
        //     HttpMethod.DELETE,
        //     null,
        //     Void.class
        // );
        // assertEquals(HttpStatus.NO_CONTENT, deleteItemResponse.getStatusCode());

        // // Step 10: Verify loan and item deletion
        // assertFalse(loanRepository.existsById(loanId));
        // assertFalse(itemRepository.existsById(itemId));
    
    }
}
