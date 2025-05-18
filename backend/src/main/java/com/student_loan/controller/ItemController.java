package com.student_loan.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.student_loan.dtos.ItemRecord;
import com.student_loan.model.Item;
import com.student_loan.model.User;
import com.student_loan.service.ItemService;
import com.student_loan.service.LoanService;
import com.student_loan.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing items in the system. Handles HTTP requests
 * related to item operations such as creating, updating, deleting, and
 * retrieving items.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
	
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
	@Autowired
    private LoanService loanService;

	/**
	 * Constructor for ItemController.
	 *
	 * @param itemService Service for item operations.
	 * @param userService Service for user operations.
	 * @param loanService Service for loan operations.
	 */
	@Autowired
	public ItemController(ItemService itemService, UserService userService, LoanService loanService) {
		this.itemService = itemService;
		this.userService = userService;
		this.loanService = loanService;
	}

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
	 * Retrieves all items in the system.
	 *
	 * @return List of all items.
	 */
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

    	User user = userService.getUserByEmail(email);
        if (user == null) { // || user.getAdmin()==false) {  Temporal
			return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
        }
    	
    	return new ResponseEntity<>(itemService.getAllItems(),HttpStatus.OK);
    }

    /**
     * Retrieves all available items in the system.
     *
     * @return ResponseEntity containing a list of available items.
     * 
     * @GetMapping("/available")
    public ResponseEntity<List<Item>> getAvailableItems() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

    	User user = userService.getUserByEmail(email);
        if (user == null) {
			return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
        }
    	
    	return new ResponseEntity<>(itemService.getAvailableItems(),HttpStatus.OK);
    }

     */

	
	 /**
     * Retrieves all items owned by a specific user.
     *
     * @param id The ID of the user.
     * @return ResponseEntity containing a list of items owned by the user.
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<List<Item>> getItemsByUser(@PathVariable Long id) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

    	User user = userService.getUserByEmail(email);
		
        if (user == null) { // || (user.getAdmin()==false && user.getId()!=id)) { TODO Uncomment to enable a user only to see its own items
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else {
        	try {
    			List<Item> items = itemService.getItemsByUser(id);
    			return new ResponseEntity<>(items, HttpStatus.OK);
			} catch (RuntimeException e) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
        }
    }
    
	/**
	 * Retrieves all available items in the system using a token for authentication.
	 *
	 * @param token The authentication token.
	 * @return ResponseEntity containing a list of available items.
	 */
    @GetMapping("/available")
    public ResponseEntity<List<Item>> getAvailableItems(@RequestParam("token") String token) {
    	User user = userService.getUserByToken(token);
        if (user == null) {
        	return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
        }
        List<Item> items = itemService.getItemsByAvailability(Item.ItemStatus.AVAILABLE);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
    
/**
 * Retrieves an item by its ID.
 *
 * @param id The ID of the item.
 * @return ResponseEntity containing the item.
 */
    @GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		User user = userService.getUserByEmail(email);
		Optional<Item> optionalItem = itemService.getItemById(id);

		if (user == null || optionalItem.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // o 404 si prefieres
		}

		Item item = optionalItem.get();

		if (!item.getOwner().equals(user.getId())) { // TODO && !user.getAdmin()) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(item, HttpStatus.OK);
	}
    
    /**
     * Retrieves all items lent by the authenticated user.
     *
     * @return ResponseEntity containing a list of lent items.
     */

	@GetMapping("/lent")
	public ResponseEntity<List<Item>> getLentItemsByUser() {
		// Obtener el usuario autenticado desde el SecurityContext
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName(); // Usamos el email como identificador

		// Buscar al usuario por el email
		User user = userService.getUserByEmail(email);
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		try {
			// Obtener los items que el usuario ha prestado
			List<Long> lentItemsId = loanService.getLentItemsIdByUser(user.getId());
			List<Item> lentItems = itemService.getItemsById(lentItemsId);

			return new ResponseEntity<>(lentItems, HttpStatus.OK);
		} catch (RuntimeException e) {
			// Si ocurre un error, devolvemos un error 500
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
     * Retrieves all items borrowed by the authenticated user.
     *
     * @return ResponseEntity containing a list of borrowed items.
     */
	@GetMapping("/borrowed")
	public ResponseEntity<List<Item>> getBorrowedItemsByUser() {
		// Obtener el usuario autenticado desde el SecurityContext
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName(); // Usamos el email como identificador

		// Buscar al usuario por el email
		User user = userService.getUserByEmail(email);
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		try {
			// Obtener los items que el usuario ha prestado
			List<Long> lentItemsId = loanService.getBorrowedItemsIdByUser(user.getId());
			List<Item> lentItems = itemService.getItemsById(lentItemsId);

			return new ResponseEntity<>(lentItems, HttpStatus.OK);
		} catch (RuntimeException e) {
			// Si ocurre un error, devolvemos un error 500
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	 /**
     * Creates a new item.
     *
     * @param itemRecord The item data.
     * @return ResponseEntity indicating the result of the operation.
     */


    @PostMapping("/create")
    public ResponseEntity<String> createItem(@RequestBody ItemRecord itemRecord) {
        User user = getAuthenticatedUser();
 		if (user == null) {
 			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
 		}
 		
 		Item item = convertToItem(itemRecord);
 		item.setOwner(user.getId());
 		try {
 			itemService.saveItem(item);
        } catch (RuntimeException e) {
             return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
         }
 		// If the item is created successfully, return a 201 Created response
 		return new ResponseEntity<>(HttpStatus.CREATED);
    }

	

    /**
     * Creates a new item using a token for authentication.
     *
     * @param itemRecord The item data.
     * @param token      The authentication token.
     * @return ResponseEntity indicating the result of the operation.
     */
	@PostMapping(params = "token")
     public ResponseEntity<String> createItem(@RequestBody ItemRecord itemRecord, @RequestParam("token") String token) {
         User user = userService.getUserByToken(token);
         if (user == null) {
             return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
         }
         Item item = convertToItem(itemRecord);
         item.setOwner(user.getId());
         try {
             itemService.saveItem(item);
         } catch (RuntimeException e) {
             return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
         }
         return new ResponseEntity<>(HttpStatus.CREATED);
     }

	
	 /**
     * Deletes an item by its ID.
     *
     * @param id    The ID of the item.
     * @param token The authentication token.
     * @return ResponseEntity indicating the result of the operation.
     */
	
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id, @RequestParam("token") String token) {
        User user = userService.getUserByToken(token);
        if (user == null || user.getAdmin()==false && user.getId()!=itemService.getItemById(id).get().getOwner()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		
        }
    	
    	itemService.deleteItem(id);
    	return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * Updates an item by its ID.
     *
     * @param id       The ID of the item.
     * @param item     The updated item data.
     * @param token    The authentication token.
     * @return ResponseEntity indicating the result of the operation.
     */
    @PutMapping("/{id}")
	public ResponseEntity<String> updateItem(@PathVariable Long id, @RequestBody ItemRecord item, @RequestParam("token") String token) {
    	User user = userService.getUserByToken(token);
		if (user == null || user.getId()!=Long.valueOf(itemService.getItemById(id).get().getOwner()) && user.getAdmin()!=true) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
    	try {
    		Item itemToModify = itemService.getItemById(id).get();
			itemToModify.setName(item.name() == null ? itemToModify.getName() : item.name() );
			itemToModify.setDescription(item.description() == null ? itemToModify.getDescription() : item.description() );
			itemToModify.setCategory(item.category() == null ? itemToModify.getCategory() : item.category());
			itemToModify.setImage(item.imageUrl() == null ? itemToModify.getImage() : item.imageUrl());
			itemToModify.setStatus(Item.ItemStatus.valueOf(item.status()==null ? itemToModify.getStatus().toString() : item.status()));
			itemToModify.setCondition(Item.ItemCondition.valueOf(item.condition()==null ? itemToModify.getCondition().toString() : item.condition()));
			itemService.saveItem(itemToModify);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
	}

    /**
     * Converts an ItemRecord to an Item entity.
     *
     * @param itemRecord The item record.
     * @return The converted Item entity.
     */
    private Item convertToItem(ItemRecord itemRecord) {
		Item item = new Item();
		item.setName(itemRecord.name());
		item.setDescription(itemRecord.description());
		item.setCategory(itemRecord.category());
		item.setImage(itemRecord.imageUrl());
		item.setStatus(Item.ItemStatus.valueOf(itemRecord.status().toUpperCase()));
		item.setPurchaseDate(new java.util.Date());
		item.setPurchasePrice(0.0);
		item.setCondition(Item.ItemCondition.NEW);
		return item;
    }
}