package com.student_loan.performance.service;

import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestActiveConfig;
import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.JUnitPerfReportingConfig;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Assertions;


import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.service.ItemService;
import com.student_loan.service.LoanService;
import com.student_loan.service.UserService;
import com.student_loan.model.User;
import com.student_loan.model.Item;
import com.student_loan.model.Loan;


@SpringBootTest
@ExtendWith(JUnitPerfInterceptor.class) //Medimos el rendimiento con JUnitPerf
public class PerformanceServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private LoanService loanService;

    @JUnitPerfTestActiveConfig //Para guardar los reportes generados por ContiPerf en target/reports/perf-report.html 
    private final static JUnitPerfReportingConfig PERF_CONFIG = JUnitPerfReportingConfig.builder()
        .reportGenerator(new HtmlReportGenerator(System.getProperty("user.dir") + "/target/reports/perf-report.html"))
        .build();


    // 1. TESTS SOBRE USERSERVICE    
    //Test exitoso : getAllUsers() Obtener todos los usuarios
    @Test
    @JUnitPerfTest(threads = 10, durationMs = 5000, warmUpMs = 1000)
    @JUnitPerfTestRequirement(executionsPerSec = 30, percentiles = "95:200ms")
    public void testGetAllUsersPerformance() {
        userService.getAllUsers();
    }

    //Test exitoso : login válido
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 5000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testValidLoginPerformance() {
        CredentialsDTO credentials = new CredentialsDTO("ana.garcia@email.com", "dF!94vH*2kQ#bR");
        userService.login(credentials);
    }

     // Test fallido: login con credenciales inválidas
     @Test
     @JUnitPerfTest(threads = 5, durationMs = 3000)
     @JUnitPerfTestRequirement(executionsPerSec = 5, allowedErrorPercentage = 0.30f)
     public void testInvalidLoginPerformance() {
        CredentialsDTO credentials = new CredentialsDTO("emailnoexiste@email.com", "contrasenamala");
        userService.login(credentials);
     }

    // Prueba fallida: getUserById con ID inválido
    @Test
    @JUnitPerfTest(threads = 3, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testGetUserByInvalidId() {
        userService.getUserById(9999L);
    }


    // 2. TESTS SOBRE ITEMSERVICE
    // Test exitoso: getAllItems()
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 4000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testGetAllItemsPerformance() {
        itemService.getAllItems();
    }

    //Test exitoso: getItemsByUser(). Probamos con el primer user de nuestra base de datos
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 4000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testGetItemsByUserValidUserPerformance() {
        Long validUserId = userService.getAllUsers().stream()
            .filter(user -> user.getId() != null)
            .findFirst()
            .orElseThrow()
            .getId();

        itemService.getItemsByUser(validUserId);
    }

    // Test fallido: getItemsByUser() no existe un user con ese id en nuestra base de datos
    @Test
    @JUnitPerfTest(threads = 3, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testGetItemsByUserInvalidUserPerformance() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            itemService.getItemsByUser(9999L); // ID no existe en nuestra bbdd
    });
    }

    //Test exitoso: saveItem(). Guardar un item nuevo
    @Test
    @JUnitPerfTest(threads = 4, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testSaveItemPerformance() {
        Long validUserId = userService.getAllUsers().stream()
            .filter(user -> user.getId() != null)
            .findFirst()
            .orElseThrow()
            .getId();

        Item newItem = new Item();
        newItem.setName("Test Item");
        newItem.setOwner(validUserId);

        itemService.saveItem(newItem);
}



    // Test fallido : saveItem() El dueño (usuario) no existe
    @Test
    @JUnitPerfTest(threads = 2, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 2)
    public void testSaveItemInvalidOwnerPerformance() {
        Item newItem = new Item();
        newItem.setName("Invalid Owner Item");
        newItem.setOwner(9999L); // Usuario no existe

        Assertions.assertThrows(RuntimeException.class, () -> {
            itemService.saveItem(newItem);
        });
    }

    //Test fallido: createItem() El item está duplicado
    @Test
    @JUnitPerfTest(threads = 3, durationMs = 3000)
    @JUnitPerfTestRequirement(executionsPerSec = 3)
    public void testCreateItemDuplicatePerformance() {
        Item existingItem = itemService.getAllItems().stream()
            .filter(i -> i.getId() != null)
            .findFirst()
            .orElseThrow();

        Assertions.assertThrows(RuntimeException.class, () -> {
            itemService.createItem(existingItem);
        });
    }


    // 3. TEST SOBRE LOAN SERVICE

    //Test exitoso: obtener todos los prestamos
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 5000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testGetAllLoansPerformance() {
        loanService.getAllLoans();
    }


    //Test exitoso: obtener prestamos de un prestamista especifico
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 5000)
    @JUnitPerfTestRequirement(executionsPerSec = 10)
    public void testGetLoansByLenderPerformance() {
        Long lenderId = userService.getAllUsers().get(0).getId(); 
        loanService.getLoansByLender(lenderId);
    }

    // Test fallido : el usuario del que queremos conseguir prestamos no existe
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 5000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testGetLoansByLenderInvalidUserPerformance() {
        try {
            loanService.getLoansByLender(9999L);  // Usuario no existente
        } catch (RuntimeException e) {
        // Esperamos esta excepción, ya que no existe el usuario
        }
    }

    // Test exitoso: cerar un préstamo
    @Test
    @JUnitPerfTest(threads = 3, durationMs = 4000)
    @JUnitPerfTestRequirement(executionsPerSec = 5)
    public void testCreateLoanPerformance() {
        Loan newLoan = new Loan();
        newLoan.setLender(userService.getAllUsers().get(0).getId());
        newLoan.setBorrower(userService.getAllUsers().get(1).getId());
        newLoan.setItem(itemService.getAllItems().get(0).getId());
        newLoan.setLoanStatus(Loan.Status.IN_USE);

        loanService.createLoan(newLoan);
    }

    
}
