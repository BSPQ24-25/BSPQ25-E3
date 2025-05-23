package com.student_loan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.student_loan.model.Item;
import com.student_loan.model.Item.ItemStatus;
import com.student_loan.model.Loan;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.LoanRepository;
import com.student_loan.repository.UserRepository;
import com.student_loan.dtos.LoanAndItemDto;

/**
 * Service class for managing items in the system.
 * Handles operations such as retrieving, creating, saving, and deleting items.
 */
@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoanRepository loanRepository;
    
    /**
     * Retrieves all items from the repository.
     *
     * @return List of all items.
     */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /**
     * Retrieves only items with AVAILABLE status.
     *
     * @return List of available items.
     */
	public List<Item> getAvailableItems() {
        return itemRepository.findByStatus(ItemStatus.AVAILABLE);
    }

	 /**
     * Finds an item by its ID.
     *
     * @param id The item ID.
     * @return Optional containing the item if found, or empty if not.
     */
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    /**
     * Finds a list of items by their IDs.
     *
     * @param itemsId List of item IDs.
     * @return List of found items.
     */
	public List<Item> getItemsById(List<Long> itemsId) {
		List<Item> items = new ArrayList<>();
		for (Long id : itemsId) {
			Optional<Item> optionalItem = getItemById(id);
			optionalItem.ifPresent(items::add);
		}
		return items;
	}
	
	 /**
     * Retrieves all items owned by a specific user.
     *
     * @param id User ID.
     * @return List of items owned by the user.
     * @throws RuntimeException if the user is not found.
     */
    
    public List<Item> getItemsByUser(Long id){
    	if(userRepository.findById(id).isPresent()) {
    		return itemRepository.findByOwner(id);
    	}else {
    		throw new RuntimeException("Failed to get items! User not found with id: " + id);
    	}
    }
    
    /**
     * Saves an item to the repository.
     *
     * @param item The item to save.
     * @return The saved item.
     * @throws RuntimeException if the item's owner does not exist.
     */
	public List<Item> getItemsByAvailability(ItemStatus status) {
		return itemRepository.findByStatus(status);
	}
    public Item saveItem(Item item) {
    	if(!userRepository.findById(item.getOwner()).isPresent()) {
    		throw new RuntimeException("Failed to save item with id "+ item.getId()+": Owner not found with id: " + item.getOwner());
    	}else {
	        return itemRepository.save(item);
	    }
    }

    public List<LoanAndItemDto> getItemsLentByUserWithActiveLoans(Long userId) {
        List<Loan> loans = loanRepository.findByLenderAndLoanStatus(userId, Loan.Status.IN_USE);
        
        return loans.stream()
            .map(loan -> {
                Item item = itemRepository.findById(loan.getItem())
                                        .orElseThrow(() -> new RuntimeException("Item no encontrado"));
                LoanAndItemDto loanItemDto = new LoanAndItemDto();
                loanItemDto.setLoanId(loan.getId());
                loanItemDto.setBorrowerId(loan.getBorrower());
                loanItemDto.setLenderId(loan.getLender());
                loanItemDto.setStartDate(loan.getLoanDate());
                loanItemDto.setEndDate(loan.getEstimatedReturnDate());
                loanItemDto.setItemId(item.getId());
                loanItemDto.setItemName(item.getName());
                loanItemDto.setItemDescription(item.getDescription());
                return loanItemDto;
            })
            .collect(Collectors.toList());
    }

    public List<LoanAndItemDto> getItemsBorrowedByUserWithActiveLoans(Long userId) {
        List<Loan> loans = loanRepository.findByBorrowerAndLoanStatus(userId, Loan.Status.IN_USE);
        
        return loans.stream()
            .map(loan -> {
                Item item = itemRepository.findById(loan.getItem())
                                        .orElseThrow(() -> new RuntimeException("Item no encontrado"));
                LoanAndItemDto loanItemDto = new LoanAndItemDto();
                loanItemDto.setLoanId(loan.getId());
                loanItemDto.setBorrowerId(loan.getBorrower());
                loanItemDto.setLenderId(loan.getLender());
                loanItemDto.setStartDate(loan.getLoanDate());
                loanItemDto.setEndDate(loan.getEstimatedReturnDate());
                loanItemDto.setItemId(item.getId());
                loanItemDto.setItemName(item.getName());
                loanItemDto.setItemDescription(item.getDescription());
                return loanItemDto;
            })
            .collect(Collectors.toList());
    }
    
	/**
	 * Creates a new item in the repository.
	 *
	 * @param item The item to create.
	 * @return The created item.
	 * @throws RuntimeException if the item already exists or the owner does not
	 *                          exist.
	 */
    
	public Item createItem(Item item) {
		Optional<Item> optionalItem = itemRepository.findById(item.getId());
		if (optionalItem.isPresent()) {
			throw new RuntimeException(
					"Failed to create item with id " + item.getId() + ": Item already exists with id: " + item.getId());
		}else if(!userRepository.findById(item.getOwner()).isPresent()) {
    		throw new RuntimeException("Failed to save item with id "+ item.getId()+": Owner not found with id: " + item.getOwner());
    	}
		return itemRepository.save(item);
	}
	    
	
	  /**
     * Deletes an item by its ID.
     *
     * @param id The ID of the item to delete.
     */
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
