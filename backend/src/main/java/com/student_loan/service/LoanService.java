package com.student_loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.student_loan.model.Loan;
import com.student_loan.model.Loan.Status;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.LoanRepository;
import com.student_loan.repository.UserRepository;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing loans between users.
 * Provides methods for creating, retrieving, updating, and deleting loans.
 */
@Service
public class LoanService {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    /**
     * Retrieves all loans in the system.
     *
     * @return List of all loans.
     */
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    /**
     * Finds a loan by its ID.
     *
     * @param id Loan ID.
     * @return Optional containing the loan if found.
     */
    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }
    
    
    /**
     * Gets all loans where the given user is the lender.
     *
     * @param userId User ID of the lender.
     * @return List of loans.
     */
    
	public List<Loan> getLoansByLender(Long userId) {
		return loanRepository.findByLender(userId);
	}
	
	/**
     * Gets all loans where the given user is the borrower.
     *
     * @param userId User ID of the borrower.
     * @return List of loans.
     */
	public List<Loan> getLoansByBorrower(Long userId) {
		return loanRepository.findByBorrower(userId);
	}
	
	  /**
     * Gets IDs of items currently lent by a user.
     *
     * @param userId User ID of the lender.
     * @return List of item IDs.
     */
	
	public List<Long> getLentItemsIdByUser(Long userId) {

        // Obtener los préstamos activos donde el usuario es el prestamista
        List<Loan> loans = loanRepository.findByLenderAndLoanStatus(userId, Status.IN_USE);

        // Extraer los items de los préstamos activos
        List<Long> lentItems = new ArrayList<>();
        for (Loan loan : loans) {
            lentItems.add(loan.getItem());
        }
        
        return lentItems;
    }

	/**
	 * Gets IDs of items currently borrowed by a user.
	 *
	 * @param userId User ID of the borrower.
	 * @return List of item IDs.
	 */
	
	public List<Long> getBorrowedItemsIdByUser(Long userId) {

        // Obtener los préstamos activos donde el usuario es el prestamista
        List<Loan> loans = loanRepository.findByBorrowerAndLoanStatus(userId, Status.IN_USE);

        // Extraer los items de los préstamos activos
        List<Long> lentItems = new ArrayList<>();
        for (Loan loan : loans) {
            lentItems.add(loan.getItem());
        }
        
        return lentItems;
    }

	/**
	 * Saves a loan to the repository.
	 *
	 * @param loan Loan object to save.
	 * @return Saved loan object.
	 * @throws RuntimeException if the lender, borrower, or item is not found.
	 */

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

	/**
	 * Updates an existing loan.
	 *
	 * @param loan Loan object with updated information.
	 * @return Updated loan object.
	 * @throws RuntimeException if the loan is not found.
	 */

    public boolean returnLoan(Long itemId, Long borrowerId) {
        // Buscamos el préstamo que cumpla todas las condiciones:
        Optional<Loan> optionalLoan = loanRepository.findByBorrowerAndItemAndLoanStatus(borrowerId, itemId, Loan.Status.IN_USE);
    
        if (optionalLoan.isPresent()) {
            Loan loan = optionalLoan.get();
            
            // Actualizamos el estado de la loan y la fecha de devolución
            loan.setLoanStatus(Loan.Status.RETURNED);
            loan.setRealReturnDate(new Date()); // Fecha de devolución actual
    
            // Guardamos el cambio en la base de datos
            loanRepository.save(loan);
            
            System.out.println("Saved Loan: " + loan.getLoanStatus());
            return true;
        } else {
            return false;
        }
    }
    	

    /**
     * Deletes a loan by its ID.
     *
     * @param id The ID of the loan to delete.
     */

    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }
    /**
     * Creates a new loan if it does not already exist.
     *
     * @param loan The loan to create.
     * @return The created loan.
     * @throws RuntimeException if lender, borrower, or item does not exist.
     */
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
