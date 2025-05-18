package com.student_loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import com.student_loan.model.Item;
import com.student_loan.model.Loan;
import com.student_loan.model.Loan.Status;
import com.student_loan.model.User;
import com.student_loan.model.Item.ItemStatus;
import com.student_loan.model.Item;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.LoanRepository;
import com.student_loan.repository.UserRepository;

import java.util.Date;
import java.util.ArrayList;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
	@Autowired
	private LoanRepository loanRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private NotificationService notificationService;

	
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LoanService.class);
	
	public List<Loan> getAllLoans() {
		return loanRepository.findAll();
	}

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> getLoansByLender(Long userId) {
        return loanRepository.findByLender(userId);
    }

    public List<Loan> getLoansByBorrower(Long userId) {
        return loanRepository.findByBorrower(userId);
    }

    public List<Long> getLentItemsIdByUser(Long userId) {
        List<Loan> loans = loanRepository.findByLenderAndLoanStatus(userId, Status.IN_USE);
        List<Long> lentItems = new ArrayList<>();
        for (Loan loan : loans) {
            lentItems.add(loan.getItem());
        }
        return lentItems;
    }

    public List<Long> getBorrowedItemsIdByUser(Long userId) {
        List<Loan> loans = loanRepository.findByBorrowerAndLoanStatus(userId, Status.IN_USE);
        List<Long> borrowedItems = new ArrayList<>();
        for (Loan loan : loans) {
            borrowedItems.add(loan.getItem());
        }
        return borrowedItems;
    }


    public Loan saveLoan(Loan loan) {
        // Validate lender first to satisfy tests
        Optional<User> lenderOpt = userRepository.findById(loan.getLender());
        if (lenderOpt == null || lenderOpt.isEmpty()) {
            throw new RuntimeException("Failed to save loan with id " + loan.getId() + ": Lender not found with id: " + loan.getLender());
        }

        // Validate borrower
        Optional<User> borrowerOpt = userRepository.findById(loan.getBorrower());
        if (borrowerOpt == null || borrowerOpt.isEmpty()) {
            throw new RuntimeException("Failed to save loan with id " + loan.getId() + ": Borrower not found with id: " + loan.getBorrower());
        }
        User borrower = borrowerOpt.get();

        if (borrower.hasPenalty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cannot borrow items while under penalty."
            );
        }

        // Validate item
        Optional<Item> itemOpt = itemRepository.findById(loan.getItem());
        if (itemOpt == null || itemOpt.isEmpty()) {
            throw new RuntimeException("Failed to save loan with id " + loan.getId() + ": Item not found with id: " + loan.getItem());
        }

        // Check active loans limit on new loans
        if (loan.getId() == null && loan.getLoanStatus() == Loan.Status.IN_USE) {
            int activos = loanRepository.countByBorrowerAndLoanStatus(
                loan.getBorrower(), Loan.Status.IN_USE);
            if (activos >= 3) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Failed to save loan with id " + loan.getId() + ": You already have 3 items reserved. Return an item before booking another."
                );
            }
        }

        // Change item's status in DB
        Item itemToModify = itemOpt.get();
        itemToModify.setStatus(ItemStatus.BORROWED);
        itemRepository.save(itemToModify);

        // Save loan
        return loanRepository.save(loan);
    }


    public boolean returnLoan(Long itemId, Long borrowerId) {
    	Optional<Loan> optionalLoan = loanRepository.findByBorrowerAndItemAndLoanStatus(
                borrowerId, itemId, Loan.Status.IN_USE);

        if (optionalLoan != null && optionalLoan.isPresent()) {
            // Update item status
            Optional<Item> optionalItem = itemRepository.findById(itemId);
            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();
                item.setStatus(ItemStatus.AVAILABLE);
                itemRepository.save(item);
            }
            // Update loan status
            Loan loan = optionalLoan.get();
            loan.setLoanStatus(Loan.Status.RETURNED);
            loan.setRealReturnDate(new Date());
    		Item item = itemRepository.findById(loan.getItem()).get();
    		item.setStatus(Item.ItemStatus.AVAILABLE);
    		itemRepository.save(item);
            loanRepository.save(loan);
         // Mail to lender
            if(userRepository.findById(loan.getLender()).isPresent() && userRepository.findById(loan.getBorrower()).isPresent()) {
    		notificationService.enviarCorreo(userRepository.findById(loan.getLender()).get().getEmail(), "Item returned",
    				"Your item has been returned!\nItem: " + itemRepository.findById(loan.getItem()).get().getName()
    						+ "\nBorrower: " + userRepository.findById(loan.getBorrower()).get().getName()
    						+ "\nReturn date: " + loan.getRealReturnDate().toString()
    						+ "\n\nThank you for using our service!");
    		// Mail to borrower
    		notificationService.enviarCorreo(userRepository.findById(loan.getBorrower()).get().getEmail(), "Item returned",
    				"You have returned the item!\nItem: " + itemRepository.findById(loan.getItem()).get().getName()
    						+ "\nLender: " + userRepository.findById(loan.getLender()).get().getName() + "\nReturn date: "
    						+ loan.getRealReturnDate().toString() + "\n\nThank you for using our service!");
            }

            return true;
        } else {
        	return false;
        }
		
		
	}

    public Loan createLoan(Loan loan) {
        if (loan.getId() != null) {
            if (loanRepository.existsById(loan.getId())) {
                throw new RuntimeException("Loan already exists with id: " + loan.getId());
            }
        }

        User lender = userRepository.findById(loan.getLender())
            .orElseThrow(() -> new RuntimeException(
                "Failed to save loan: Lender not found with id: " + loan.getLender()));

        User borrower = userRepository.findById(loan.getBorrower())
            .orElseThrow(() -> new RuntimeException(
                "Failed to save loan: Borrower not found with id: " + loan.getBorrower()));

        if (borrower.hasPenalty()) {
            throw new RuntimeException("Cannot borrow items while under penalty.");
        }

        Item item = itemRepository.findById(loan.getItem())
            .orElseThrow(() -> new RuntimeException(
                "Failed to save loan: Item not found with id: " + loan.getItem()));

        if (loan.getId() == null && loan.getLoanStatus() == Loan.Status.IN_USE) {
            int activos = loanRepository.countByBorrowerAndLoanStatus(loan.getBorrower(), Loan.Status.IN_USE);
            if (activos >= 3) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You already have 3 items reserved. Return an item before booking another.");
            }
        }

        loan.setLoanStatus(Loan.Status.IN_USE);

        notificationService.enviarCorreo(
            borrower.getEmail(),
            "Loan Created",
            "You have successfully made a loan!\nItem: " + item.getName() +
            "\nLender: " + lender.getName() +
            "\nReturn date: " + loan.getEstimatedReturnDate().toString() +
            "\n\nThank you for using our service!"
        );

        notificationService.enviarCorreo(
            lender.getEmail(),
            "Item lended",
            "Your item has successfully been lended!\nItem: " + item.getName() +
            "\nBorrower: " + borrower.getName() +
            "\nReturn date: " + loan.getEstimatedReturnDate().toString() +
            "\n\nThank you for using our service!"
        );

        return loanRepository.save(loan);
    }
    
	public void deleteLoan(Long id) {
		loanRepository.deleteById(id);
	}

	
}
