package com.student_loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.student_loan.model.Loan;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.LoanRepository;
import com.student_loan.repository.UserRepository;

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
		return loanRepository.findByLender(userId);
	}
	

    public Loan saveLoan(Loan loan) {
    	
    	if(userRepository.findById(loan.getLender()) == null) {
            throw new RuntimeException("Failed to save loan with id "+ loan.getId()+": Lender not found with id: " + loan.getLender());
		}else if(userRepository.findById(loan.getBorrower())==null) {
			throw new RuntimeException("Failed to save loan with id "+ loan.getId()+": Borrower not found with id: " + loan.getBorrower());
		}else if (itemRepository.findById(loan.getItem())==null) {
			throw new RuntimeException("Failed to save loan with id "+ loan.getId()+": Item not found with id: " + loan.getItem());
		}else {
	    	
	    	return loanRepository.save(loan);
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
