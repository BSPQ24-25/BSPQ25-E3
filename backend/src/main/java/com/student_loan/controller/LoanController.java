package com.student_loan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/loans")
public class LoanController {
    @Autowired
    private LoanService loanService;
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans(@RequestParam("token") String token) {
    	User user = userService.getUserByToken(token);
        if (user == null || user.getAdmin()==false) {
        	return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
        }
        return  new ResponseEntity<>(loanService.getAllLoans(),HttpStatus.OK);
    }

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

    @PostMapping
    public ResponseEntity<String> createLoan(@RequestBody LoanRecord loan, @RequestParam("token") String token) {
    	User user = userService.getUserByToken(token);
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		Loan loanEntity = convertToLoan(loan);
		try {
			loanService.saveLoan(loanEntity);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
		// If the loan is created successfully, return a 201 Created response
		return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
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
    
	private Loan convertToLoan(LoanRecord loanRecord) {
        return new Loan(
        	null,
        	loanRecord.lender(),
            loanRecord.borrower(),
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
