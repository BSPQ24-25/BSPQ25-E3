package com.student_loan.unit.dtos;

import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.dtos.ItemRecord;
import com.student_loan.dtos.LoanRecord;
import com.student_loan.dtos.RankingDTO;
import com.student_loan.dtos.RegistrationRecord;
import com.student_loan.dtos.UserRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DtoTests {

    @Test
    @DisplayName("CredentialsDTO getters and setters should work")
    void testCredentialsDTO() {
        CredentialsDTO creds = new CredentialsDTO();
        
        creds.setEmail("user@example.com");
        creds.setPassword("secret");

        assertEquals("user@example.com", creds.getEmail());
        assertEquals("secret", creds.getPassword());

        CredentialsDTO creds2 = new CredentialsDTO("a@b.com", "pwd");
        assertEquals("a@b.com", creds2.getEmail());
        assertEquals("pwd", creds2.getPassword());
    }

    @Test
    @DisplayName("ItemRecord should return correct values and have proper equals/hashCode/toString")
    void testItemRecord() {
        ItemRecord item = new ItemRecord(
            "Item1", "Desc", "Cat", "5","http://img", "Available", "New"
        );
        assertEquals("Item1", item.name());
        assertEquals("Desc", item.description());
        assertEquals("Cat", item.category());
        assertEquals("http://img", item.imageBase64());
        assertEquals("Available", item.status());
        assertEquals("New", item.condition());

        ItemRecord same = new ItemRecord(
            "Item1", "Desc", "Cat", "5","http://img", "Available", "New"
        );
        assertEquals(item, same);
        assertEquals(item.hashCode(), same.hashCode());
        assertTrue(item.toString().contains("Item1"));
    }

    @Test
    @DisplayName("LoanRecord should return correct values and support equals/hashCode")
    void testLoanRecord() {
        LoanRecord loan = new LoanRecord(
            1L, 2L, 3L, 4L,
            "2025-01-01", "2025-02-01", null,
            "ONGOING", "5", "None"
        );
        assertEquals(1L, loan.id());
        assertEquals(2L, loan.lender());
        assertEquals(3L, loan.borrower());
        assertEquals(4L, loan.item());
        assertEquals("2025-01-01", loan.loanDate());
        assertEquals("2025-02-01", loan.estimatedReturnDate());
        assertNull(loan.realReturnDate());
        assertEquals("ONGOING", loan.loanStatus());
        assertEquals("5", loan.rating());
        assertEquals("None", loan.observations());

        LoanRecord sameLoan = new LoanRecord(
            1L, 2L, 3L, 4L,
            "2025-01-01", "2025-02-01", null,
            "ONGOING", "5", "None"
        );
        assertEquals(loan, sameLoan);
        assertEquals(loan.hashCode(), sameLoan.hashCode());
    }

    @Test
    @DisplayName("RegistrationRecord should return correct values and support equals")
    void testRegistrationRecord() {
        RegistrationRecord reg = new RegistrationRecord(
            "John", "Doe", "john@d.com", "pwd","616238276", "Pancracion Kalea 7", "UNIVERSITY_DEGREE", 3
        );
        assertEquals("John", reg.name());
        assertEquals("Doe", reg.lastName());
        assertEquals("john@d.com", reg.email());
        assertEquals("pwd", reg.password());
        assertEquals("616238276", reg.telephoneNumber());
        assertEquals("Pancracion Kalea 7", reg.address());
        assertEquals("UNIVERSITY_DEGREE", reg.degreeType());
        assertEquals(3, reg.degreeYear());

        RegistrationRecord sameReg = new RegistrationRecord(
            "John", "Doe", "john@d.com", "pwd","616238276", "Pancracion Kalea 7", "UNIVERSITY_DEGREE", 3
        );
        assertEquals(reg, sameReg);
        assertEquals(reg.hashCode(), sameReg.hashCode());
    }

    @Test
    @DisplayName("UserRecord should return correct values")
    void testUserRecord() {
        UserRecord user = new UserRecord(
            "Jane", "Smith", "jane@ex.com", "pw",
            "12345", "Addr", "MASTER", 2024, 1, 4.0, false
        );
        assertEquals("Jane", user.name());
        assertEquals("Smith", user.lastName());
        assertEquals("jane@ex.com", user.email());
        assertEquals("pw", user.password());
        assertEquals("12345", user.telephoneNumber());
        assertEquals("Addr", user.address());
        assertEquals("MASTER", user.degreeType());
        assertEquals(2024, user.degreeYear());
        assertEquals(1, user.penalties());
        assertEquals(4.0, user.averageRating());
        assertFalse(user.admin());

        UserRecord sameUser = new UserRecord(
            "Jane", "Smith", "jane@ex.com", "pw",
            "12345", "Addr", "MASTER", 2024, 1, 4.0, false
        );
        assertEquals(user, sameUser);
    }

    @Test
    @DisplayName("RankingDTO getters should return correct values")
    void testRankingDTO() {
        RankingDTO ranking = new RankingDTO(10L, "TestUser", 4.75, 2);
        assertEquals(10L, ranking.getUserId());
        assertEquals("TestUser", ranking.getName());
        assertEquals(4.75, ranking.getAverageRating());
        assertEquals(2, ranking.getPenalties());
    }
}
