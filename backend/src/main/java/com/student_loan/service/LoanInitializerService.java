package com.student_loan.service;

import com.student_loan.model.Item;
import com.student_loan.model.User;
import com.student_loan.model.Loan;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.UserRepository;
import com.student_loan.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import jakarta.annotation.PostConstruct;
import java.util.Date;

@Service
public class LoanInitializerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private LoanRepository loanRepository;

    private boolean lanzado = true;

    @PostConstruct
    @Transactional
    public void insertDefaultLoans() {

        if (lanzado) {
            System.out.println("Loans are already in the database.");
            
        } else {
            // Get the ID of the User
            Long user_loan1 = userRepository.findById(1L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
            Long user_loan2 = userRepository.findById(2L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
            Long user_loan3 = userRepository.findById(3L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
            Long user_loan4 = userRepository.findById(4L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
            Long user_loan5 = userRepository.findById(5L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
            Long user_loan6 = userRepository.findById(6L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
            Long user_loan7 = userRepository.findById(7L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
            Long user_loan8 = userRepository.findById(8L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
            Long user_loan9 = userRepository.findById(9L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
            Long user_loan10 = userRepository.findById(10L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));

            // Get the ID of the Item
            Long item_loan1 = itemRepository.findById(1L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan2 = itemRepository.findById(2L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan3 = itemRepository.findById(3L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan4 = itemRepository.findById(4L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan5 = itemRepository.findById(5L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan6 = itemRepository.findById(6L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan7 = itemRepository.findById(7L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan8 = itemRepository.findById(8L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan9 = itemRepository.findById(9L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan20 = itemRepository.findById(10L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan21 = itemRepository.findById(21L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan22 = itemRepository.findById(22L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan23 = itemRepository.findById(23L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan24 = itemRepository.findById(24L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan25 = itemRepository.findById(25L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan31 = itemRepository.findById(31L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan32 = itemRepository.findById(32L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan33 = itemRepository.findById(33L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan34 = itemRepository.findById(34L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
            Long item_loan35 = itemRepository.findById(35L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));

            // Set of loans to insert
            Loan loan1 = new Loan(null, user_loan1, user_loan2, item_loan1, new Date(125, 1, 5), new Date(125, 43, 19), null, Loan.Status.IN_USE, null, "");
            Loan loan2 = new Loan(null, user_loan2, user_loan4, item_loan2, new Date(125, 2, 8), new Date(125, 4, 15), null, Loan.Status.IN_USE, null, "");
            Loan loan3 = new Loan(null, user_loan3, user_loan6, item_loan3, new Date(125, 0, 18), new Date(125, 3, 30), null, Loan.Status.IN_USE, null, "");
            Loan loan4 = new Loan(null, user_loan4, user_loan8, item_loan4, new Date(125, 1, 12), new Date(125, 1, 20), new Date(125, 1, 20), Loan.Status.RETURNED, 5.0, "Excellent");
            Loan loan5 = new Loan(null, user_loan5, user_loan10, item_loan5, new Date(125, 1, 28), new Date(125, 2, 7), new Date(125, 2, 6), Loan.Status.RETURNED, 4.8, "Returned early");
            Loan loan6 = new Loan(null, user_loan6, user_loan9, item_loan6, new Date(125, 3, 1), new Date(125, 4, 15), null, Loan.Status.IN_USE, null, "");
            Loan loan7 = new Loan(null, user_loan7, user_loan7, item_loan7, new Date(125, 3, 10), new Date(125, 5, 17), null, Loan.Status.IN_USE, null, "");
            Loan loan8 = new Loan(null, user_loan8, user_loan6, item_loan8, new Date(125, 2, 8), new Date(125, 2, 22), null, Loan.Status.LOST, null, "Item lost during loan");
            Loan loan9 = new Loan(null, user_loan9, user_loan1, item_loan9, new Date(125, 0, 10), new Date(125, 0, 18), new Date(125, 0, 19), Loan.Status.RETURNED, 4.9, "Returned with care");
            Loan loan10 = new Loan(null, user_loan5, user_loan3, item_loan20, new Date(125, 2, 3), new Date(125, 2, 10), new Date(125, 2, 12), Loan.Status.RETURNED, 4.2, "Slight delay");
            Loan loan11 = new Loan(null, user_loan6, user_loan5, item_loan21, new Date(125, 3, 1), new Date(125, 3, 10), null, Loan.Status.IN_USE, null, "");
            Loan loan12 = new Loan(null, user_loan7, user_loan4, item_loan22, new Date(125, 2, 15), new Date(125, 3, 22), null, Loan.Status.IN_USE, null, "");
            Loan loan13 = new Loan(null, user_loan8, user_loan2, item_loan23, new Date(125, 1, 5), new Date(125, 1, 12), new Date(125, 1, 11), Loan.Status.RETURNED, 4.6, "Early return");
            Loan loan14 = new Loan(null, user_loan9, user_loan6, item_loan24, new Date(125, 0, 10), new Date(125, 0, 20), new Date(125, 0, 18), Loan.Status.RETURNED, 4.8, "Returned ahead of time");
            Loan loan15 = new Loan(null, user_loan10, user_loan8, item_loan25, new Date(125, 1, 14), new Date(125, 2, 25), null, Loan.Status.IN_USE, null, "");
            Loan loan16 = new Loan(null, user_loan1, user_loan1, item_loan31, new Date(125, 2, 3), new Date(125, 2, 10), null, Loan.Status.LOST, null, "Item lost during loan");
            Loan loan17 = new Loan(null, user_loan2, user_loan7, item_loan32, new Date(125, 3, 2), new Date(125, 3, 30), null, Loan.Status.IN_USE, null, "");
            Loan loan18 = new Loan(null, user_loan3, user_loan9, item_loan33, new Date(125, 2, 17), new Date(125, 3, 24), null, Loan.Status.IN_USE, null, "");
            Loan loan19 = new Loan(null, user_loan4, user_loan10, item_loan34, new Date(125, 2, 11), new Date(125, 2, 20), new Date(125, 2, 20), Loan.Status.RETURNED, 5.0, "Excellent return");
            Loan loan20 = new Loan(null, user_loan5, user_loan5, item_loan35, new Date(125, 1, 1), new Date(125, 1, 14), new Date(125, 1, 13), Loan.Status.RETURNED, 4.2, "Returned without issues");

            // Save loans to the database
            loanRepository.save(loan1); loanRepository.save(loan2); loanRepository.save(loan3); loanRepository.save(loan4); loanRepository.save(loan5);
            loanRepository.save(loan6); loanRepository.save(loan7); loanRepository.save(loan8); loanRepository.save(loan9); loanRepository.save(loan10);
            loanRepository.save(loan11); loanRepository.save(loan12); loanRepository.save(loan13); loanRepository.save(loan14); loanRepository.save(loan15);
            loanRepository.save(loan16); loanRepository.save(loan17); loanRepository.save(loan18); loanRepository.save(loan19); loanRepository.save(loan20);

            System.out.println("Loans inserted successfully.");
        }
    }
}
