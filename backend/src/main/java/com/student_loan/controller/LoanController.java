package com.student_loan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import com.student_loan.dtos.LoanRecord;
import com.student_loan.model.Loan;
import com.student_loan.model.User;
import com.student_loan.service.LoanService;
import com.student_loan.service.UserService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for managing loans in the system. Handles HTTP requests
 * related to loan operations such as creating, updating, deleting, and
 * retrieving loans.
 */
@RestController
@RequestMapping("/loans")
public class LoanController {
    @Autowired
    private LoanService loanService;
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    /**
     * Retrieves the authenticated user from the SecurityContext.
     *
     * @return The authenticated user.
     */
	private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.getUserByEmail(email);
    }

	 /**
     * Retrieves all loans in the system.
     *
     * @param token The authentication token.
     * @return ResponseEntity containing a list of all loans.
     */
    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans(@RequestParam("token") String token) {
    	User user = userService.getUserByToken(token);
        if (user == null || user.getAdmin()==false) {
        	return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
        }
        return  new ResponseEntity<>(loanService.getAllLoans(),HttpStatus.OK);
    }
    /**
     * Retrieves a loan by its ID.
     *
     * @param id    The ID of the loan.
     * @param token The authentication token.
     * @return ResponseEntity containing the loan.
     */
    
    @GetMapping("/{id}")
	public ResponseEntity<Loan> getLoanById(@PathVariable Long id, @RequestParam("token") String token) {
    	User user = userService.getUserByToken(token);
    	if (user == null || 
        	(!user.getAdmin() && 
        	user.getId() != loanService.getLoanById(id).get().getLender() && 
        	user.getId() != loanService.getLoanById(id).get().getBorrower())) {
        	return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    	}

    	Optional<Loan> loanOpt = loanService.getLoanById(id);
    	if (loanOpt.isEmpty()) {
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	Loan loan = loanOpt.get();
    	return new ResponseEntity<>(loan, HttpStatus.OK);
	}

    /**
     * Retrieves loans by lender ID.
     *
     * @param token    The authentication token.
     * @param lenderId The ID of the lender.
     * @return ResponseEntity containing a list of loans by the lender.
     */
    @GetMapping("/lender")
    public ResponseEntity<List<Loan>> getLoansByLender(@RequestParam("token") String token, @RequestParam("lenderId") Long lenderId) {
    	User user = userService.getUserByToken(token);
        if (user == null || user.getId()!=lenderId && user.getAdmin()==false) {
			return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
        }
        try {
			List<Loan> loans = loanService.getLoansByLender(lenderId);
			return new ResponseEntity<>(loans, HttpStatus.OK);
			
			} catch (RuntimeException e) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
	}
    
    /**
     * Retrieves loans by borrower ID.
     *
     * @param token       The authentication token.
     * @param borrowerId  The ID of the borrower.
     * @return ResponseEntity containing a list of loans by the borrower.
     */
    @GetMapping("/borrower")
	public ResponseEntity<List<Loan>> getLoansByBorrower(
			@RequestParam("token") String token,
			@RequestParam("borrowerId") Long borrowerId) {

		User user = userService.getUserByToken(token);

		if (user == null || (!user.getAdmin() && !Objects.equals(user.getId(), borrowerId))) {
			logger.warn("Unauthorized access: token='{}' yielded user={}, trying to query borrowerId={}",
						token, user, borrowerId);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		List<Loan> loans = loanService.getLoansByBorrower(borrowerId);
		return new ResponseEntity<>(loans, HttpStatus.OK);
	}

    /**
     * Creates a new loan.
     *
     * @param loan  The loan data.
     * @return ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/create")
    public ResponseEntity<String> createLoan(@RequestBody LoanRecord loan) {
    	User user = getAuthenticatedUser();
        if (user == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		Loan loanEntity = convertToLoan(loan);
        loanEntity.setBorrower(user.getId());
		try {
			loanService.createLoan(loanEntity);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
		// If the loan is created successfully, return a 201 Created response
		return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /**
     * Updates a loan by its ID.
     *
     * @param id        The ID of the loan.
     * @param loan      The updated loan data.
     * @param authHeader The authorization header containing the token.
     * @return ResponseEntity indicating the result of the operation.
     */
    
    @PutMapping("/{id}")
	public ResponseEntity<String> updateLoan(@PathVariable Long id, @RequestBody LoanRecord loan, @RequestHeader("Authorization") String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        	return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    	}
		
    	String token = authHeader.substring(7);

    	User user = userService.getUserByToken(token);
    	Loan existingLoan = loanService.getLoanById(id).get();
		if (user == null || user.getId()!=existingLoan.getLender() && user.getId()!=existingLoan.getBorrower() && user.getAdmin()==false) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
    	try {
    		existingLoan.setLender(loan.lender()==null ? existingLoan.getLender() : loan.lender());
    		existingLoan.setBorrower(loan.borrower()==null ? existingLoan.getBorrower() : loan.borrower());
    		existingLoan.setItem(loan.item()==null ? existingLoan.getItem() : loan.item());
    		existingLoan.setLoanDate(loan.loanDate()==null ? existingLoan.getLoanDate() : Date.valueOf(loan.loanDate()));
    		existingLoan.setEstimatedReturnDate(loan.estimatedReturnDate()==null ? existingLoan.getEstimatedReturnDate() : Date.valueOf(loan.estimatedReturnDate()));
    		existingLoan.setRealReturnDate(loan.realReturnDate()==null ? existingLoan.getRealReturnDate() : Date.valueOf(loan.realReturnDate()));
    		existingLoan.setLoanStatus(loan.loanStatus()==null ? existingLoan.getLoanStatus() : Loan.Status.valueOf(loan.loanStatus()));
    		existingLoan.setRating(loan.rating()==null ? existingLoan.getRating() : Double.valueOf(loan.rating()));
    		existingLoan.setObservations(loan.observations()==null ? existingLoan.getObservations() : loan.observations());	
			loanService.saveLoan(existingLoan);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
	}

    /**
     * Marks a loan as returned by its item ID.
     *
     * @param itemId The ID of the item.
     * @return ResponseEntity indicating the result of the operation.
     */
	@PutMapping("/{itemId}/return")
    public ResponseEntity<Void> returnLoan(@PathVariable Long itemId) {
		User user = getAuthenticatedUser();
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		try {
			boolean updated = loanService.returnLoan(itemId, user.getId());
			if (updated) {
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	
	 /**
     * Deletes a loan by its ID.
     *
     * @param id    The ID of the loan.
     * @param token The authentication token.
     * @return ResponseEntity indicating the result of the operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLoan(@PathVariable Long id, @RequestParam("token") String token) {
    	User user = userService.getUserByToken(token);
        if (user == null || user.getId()!=loanService.getLoanById(id).get().getLender() && user.getId()!=loanService.getLoanById(id).get().getBorrower() && user.getAdmin()==false) {
        	 return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
        	  loanService.deleteLoan(id);
              return new ResponseEntity<>(HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
      
    }
    
    /**
     * Converts a LoanRecord to a Loan entity.
     *
     * @param loanRecord The loan record.
     * @return The converted Loan entity.
     */
	private Loan convertToLoan(LoanRecord loanRecord) {
        return new Loan(
        	null,
        	loanRecord.lender(),
            null, // It is setted in createLoan function
            loanRecord.item(),
            Date.valueOf(loanRecord.loanDate()),
            Date.valueOf(loanRecord.estimatedReturnDate()),
            null,
            Loan.Status.IN_USE,
            null,
            loanRecord.observations()
        );
	}
	
}
