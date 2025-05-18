package com.student_loan.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.student_loan.model.User;
import com.student_loan.service.UserService;
import com.student_loan.dtos.UserRecord;
import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.dtos.RegistrationRecord;
import com.student_loan.dtos.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for managing users in the system. Handles HTTP requests
 * related to user operations such as creating, updating, deleting, and
 * retrieving users.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    

    private UserService userService;
    /**
     * Constructor for UserController.
     *
     * @param userService Service for user operations.
     */
	public UserController(UserService userService) {
        this.userService = userService;
    }
	  /**
     * Retrieves all users in the system.
     *
     * @param token The authentication token.
     * @return ResponseEntity containing a list of all users.
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(@RequestParam("token") String token) {
    	 User user = userService.getUserByToken(token);
        if (user == null || user.getAdmin()==false) {
        	return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
        }
    	return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
    /**
     * Authenticates a user and generates a token.
     *
     * @param credentials The user's credentials.
     * @return ResponseEntity containing the authentication token or an error message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody CredentialsDTO credentials) {
        String token = userService.login(credentials);

        if (token.equals("Invalid credentials")) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
    }
    /**
     * Logs out a user by invalidating their token.
     *
     * @param token The authentication token.
     * @return ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestParam("token") String token) {
		if (userService.logout(token)) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}
    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user.
     * @return ResponseEntity containing the user data.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

    	User user = userService.getUserByEmail(email);
        if (user == null) {
        	   return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
		User retrievedUser = userService.getUserById(id).get();
		UserDTO userDTO = new UserDTO(retrievedUser.getId(), retrievedUser.getName(), retrievedUser.getEmail());

    	return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * Retrieves a user by their ID (versión completa usando UserRecord).
     *
     * GET /users/{id}/record
     *
     * @param id The ID of the user.
     * @return ResponseEntity containing el UserRecord con todos los campos.
     */
    @GetMapping("/{id}/record")
    public ResponseEntity<UserRecord> getUserRecordById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User requester = userService.getUserByEmail(email);
        if (requester == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User target = userService.getUserById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(userToUserRecord(target));
    }

	/**
     * Updates a user by their ID.
     *
     * @param id       The ID of the user.
     * @param userData The updated user data.
     * @param token    The authentication token.
     * @return ResponseEntity containing the updated user or an error status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserRecord userData, @RequestParam("token") String token) {
        User user = userService.getUserByToken(token);
		if (user == null || user.getAdmin() == false && user.getId() != id) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
    	
    	try {
            User updatedUser = userService.updateUser(id, userRecordToUser(userData));
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Registers a new user.
     *
     * @param userDTO The registration data.
     * @return ResponseEntity indicating the result of the operation.
     */
    //Register 
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRecord userDTO) {
        if (userService.register(registerRecordToUser(userDTO))) {
            return ResponseEntity.ok("User registered correctly");
        } else {
            return ResponseEntity.badRequest().body("The user already exists");
        }
    }
    /**
     * Deletes a user by their ID.
     *
     * @param id    The ID of the user.
     * @param token The authentication token.
     * @return ResponseEntity indicating the result of the operation.
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, @RequestParam("token") String token) {
        User user = userService.getUserByToken(token);
		if (user == null || user.getAdmin() == false && user.getId() != id) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
    	userService.deleteUser(id);

    	return new ResponseEntity<>(HttpStatus.OK);
    }
    

    /**
     * Converts a RegistrationRecord to a User entity.
     *
     * @param data The registration record.
     * @return The converted User entity.
     */

    public User registerRecordToUser(RegistrationRecord data) {
    	        User user = new User();
    	        user.setName(data.name() + " " + data.lastName() );
    	        user.setEmail(data.email());
    	        user.setPassword(data.password());
    	        user.setTelephoneNumber(data.telephoneNumber());
    	        user.setAddress(data.address());
    			user.setDegreeType(User.DegreeType.valueOf(data.degreeType()));
    			user.setDegreeYear(data.degreeYear());
    			user.setPenalties(0);
    			user.setAverageRating(0.0);
    			user.setAdmin(false);
    			return user;           
    }
    /**
     * Converts a UserRecord to a User entity.
     *
     * @param userDTO The user record.
     * @return The converted User entity.
     */
    
	public User userRecordToUser(UserRecord userDTO) {
		User user = new User();
		user.setName(userDTO.name()+" "+userDTO.lastName());
		user.setEmail(userDTO.email());
		user.setPassword(userDTO.password());
		user.setTelephoneNumber(userDTO.telephoneNumber());
		user.setAddress(userDTO.address());
		user.setDegreeType(User.DegreeType.UNIVERSITY_DEGREE);
		user.setDegreeYear(userDTO.degreeYear());
		user.setPenalties(userDTO.penalties());
		user.setAverageRating(userDTO.averageRating());
		user.setAdmin(userDTO.admin());
		
		return user;
	}

    public UserRecord userToUserRecord(User user) {
        String[] nameParts = user.getName().split(" ");
        String firstName = nameParts[0];
        String lastName  = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";

        return new UserRecord(
            firstName,
            lastName,
            user.getEmail(),
            user.getPassword(),
            user.getTelephoneNumber(),
            user.getAddress(),
            user.getDegreeType().name(),
            user.getDegreeYear(),
            user.getPenalties(),
            user.getAverageRating(),
            user.getAdmin()
        );
    }
}
