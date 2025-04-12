package com.student_loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.student_loan.model.Item;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }
    
    public List<Item> getItemsByUser(Long id){
    	if(userRepository.findById(id).isPresent()) {
    		return itemRepository.findByOwner(id);
    	}else {
    		throw new RuntimeException("Failed to get items! User not found with id: " + id);
    	}
    }
    
	public List<Item> getItemsByAvailability(Item.ItemStatus status) {
		return itemRepository.findByStatus(status);
	}

    public Item saveItem(Item item) {
    	if(!userRepository.findById(item.getOwner()).isPresent()) {
    		throw new RuntimeException("Failed to save item with id "+ item.getId()+": Owner not found with id: " + item.getOwner());
    	}else {
	        return itemRepository.save(item);
	    }
    }
    

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
	    
	
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
