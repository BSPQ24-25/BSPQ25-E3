package com.student_loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.student_loan.model.Loan;
import com.student_loan.model.Loan.Status;
import com.student_loan.model.User;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.LoanRepository;
import com.student_loan.repository.UserRepository;

import java.util.Date;
import java.util.ArrayList;
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

        List<Long> lentItems = new ArrayList<>();
        for (Loan loan : loans) {
            lentItems.add(loan.getItem());
        }
        
        return lentItems;
    }

    public Loan saveLoan(Loan loan) {
        Optional<User> borrowerOpt = userRepository.findById(loan.getBorrower());
        if (borrowerOpt.isEmpty()) {
            throw new RuntimeException("Failed to save loan with id " + loan.getId() + ": Borrower not found with id: " + loan.getBorrower());
        }

        User borrower = borrowerOpt.get();

        if (borrower.hasPenalty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cannot borrow items while under penalty."
            );
        }

        if (userRepository.findById(loan.getLender()).isEmpty()) {
            throw new RuntimeException("Failed to save loan with id " + loan.getId() + ": Lender not found with id: " + loan.getLender());
        }

        if (itemRepository.findById(loan.getItem()).isEmpty()) {
            throw new RuntimeException("Failed to save loan with id " + loan.getId() + ": Item not found with id: " + loan.getItem());
        }

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

        return loanRepository.save(loan);
    }


    public boolean returnLoan(Long itemId, Long borrowerId) {
        Optional<Loan> optionalLoan = loanRepository.findByBorrowerAndItemAndLoanStatus(borrowerId, itemId, Loan.Status.IN_USE);
    
        if (optionalLoan.isPresent()) {
            Loan loan = optionalLoan.get();
            
            loan.setLoanStatus(Loan.Status.RETURNED);
            loan.setRealReturnDate(new Date());
    
            loanRepository.save(loan);
            
            System.out.println("Saved Loan: " + loan.getLoanStatus());
            return true;
        } else {
            return false;
        }
    }

    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }
    
    //Create a loan
	public Loan createLoan(Loan loan) {
		
		Optional<Loan> optionalLoan = loanRepository.findById(loan.getId());
		if (!optionalLoan.isPresent()) {
			if(userRepository.findById(loan.getLender()) == null) {
	            throw new RuntimeException("Failed to create loan with id "+ loan.getId()+": Lender not found with id: " + loan.getLender());
			}else if(userRepository.findById(loan.getBorrower())==null) {
				throw new RuntimeException("Failed to create loan with id "+ loan.getId()+": Borrower not found with id: " + loan.getBorrower());
			}else if (itemRepository.findById(loan.getItem())==null) {
				throw new RuntimeException("Failed to create loan with id "+ loan.getId()+": Item not found with id: " + loan.getItem());
			}
			loan.setLoanStatus(Loan.Status.IN_USE);
			
			return loanRepository.save(loan);
			
        }else {
            throw new RuntimeException("Loan already exists with id: " + loan.getId());
        }
		
	}
}
