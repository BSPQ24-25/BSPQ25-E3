package com.student_loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.student_loan.model.Item;
import com.student_loan.model.Loan;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.LoanRepository;
import com.student_loan.repository.UserRepository;

import java.sql.Date;
import java.time.LocalDate;
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

		if (userRepository.findById(loan.getLender()) == null) {
			throw new RuntimeException(
					"Failed to save loan with id " + loan.getId() + ": Lender not found with id: " + loan.getLender());
		} else if (userRepository.findById(loan.getBorrower()) == null) {
			throw new RuntimeException("Failed to save loan with id " + loan.getId() + ": Borrower not found with id: "
					+ loan.getBorrower());
		} else if (itemRepository.findById(loan.getItem()) == null) {
			throw new RuntimeException(
					"Failed to save loan with id " + loan.getId() + ": Item not found with id: " + loan.getItem());
		} else {

			return loanRepository.save(loan);
		}

	}

	public void deleteLoan(Long id) {
		loanRepository.deleteById(id);
	}

	// Create a loan
	public Loan createLoan(Loan loan){

		if (userRepository.findById(loan.getLender()) == null) {
			throw new RuntimeException("Failed to create loan with id " + loan.getId()
					+ ": Lender not found with id: " + loan.getLender());
		} else if (userRepository.findById(loan.getBorrower()) == null) {
			throw new RuntimeException("Failed to create loan with id " + loan.getId()
					+ ": Borrower not found with id: " + loan.getBorrower());
		} else if (itemRepository.findById(loan.getItem()) == null) {
			throw new RuntimeException("Failed to create loan with id " + loan.getId()
					+ ": Item not found with id: " + loan.getItem());
		}
		loan.setLoanStatus(Loan.Status.IN_USE);

		// Mail to borrower
		notificationService.enviarCorreo(userRepository.findById(loan.getBorrower()).get().getEmail(),
				"Loan Created",
				"You have succesfully made a loan!\nItem: "
						+ itemRepository.findById(loan.getItem()).get().getName() + "\nLender: "
						+ userRepository.findById(loan.getLender()).get().getName() + "\nReturn date: "
						+ loan.getEstimatedReturnDate().toString() + "\n\nThank you for using our service!");

		// Mail to lender
		notificationService.enviarCorreo(userRepository.findById(loan.getLender()).get().getEmail(), "Item lended",
				"Your item has succesfully been lended!\nItem: "
						+ itemRepository.findById(loan.getItem()).get().getName() + "\nBorrower: "
						+ userRepository.findById(loan.getBorrower()).get().getName() + "\nReturn date: "
						+ loan.getEstimatedReturnDate().toString() + "\n\nThank you for using our service!");

		return loanRepository.save(loan);

	}
	
	public Loan returnLoan(Loan loan) {
		loan.setRealReturnDate(Date.valueOf(LocalDate.now()));
		loan.setLoanStatus(Loan.Status.RETURNED);
		Item item = itemRepository.findById(loan.getItem()).get();
		item.setStatus(Item.ItemStatus.AVAILABLE);
		itemRepository.save(item);
		// Mail to lender
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

		return loanRepository.save(loan);
	}
}
