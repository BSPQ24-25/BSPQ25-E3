package com.student_loan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.student_loan.model.User;
import com.student_loan.service.UserService;
import com.student_loan.dtos.UserRecord;
import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.dtos.RegistrationRecord;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(@RequestParam("token") String token) {
    	 User user = userService.getUserByToken(token);
        if (user == null || user.getAdmin()==false) {
        	return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
        }
    	return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody CredentialsDTO credentials) {
        String token = userService.login(credentials);

        if (token.equals("Invalid credentials")) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
    }
    
    @PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestParam("token") String token) {
		if (userService.logout(token)) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id, @RequestParam("token") String token) {
        
    	User user = userService.getUserByToken(token);
        if (user == null || user.getAdmin()==false && user.getId()!=id) {
        	   return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    	return new ResponseEntity<>(userService.getUserById(id).get(), HttpStatus.OK);
    }

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


    //Register 
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRecord userDTO) {
        if (userService.register(registerRecordToUser(userDTO))) {
            return ResponseEntity.ok("User registered correctly");
        } else {
            return ResponseEntity.badRequest().body("The user already exists");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, @RequestParam("token") String token) {
        User user = userService.getUserByToken(token);
		if (user == null || user.getAdmin() == false && user.getId() != id) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
    	userService.deleteUser(id);

    	return new ResponseEntity<>(HttpStatus.OK);
    }
    private User registerRecordToUser(RegistrationRecord data) {
    	        User user = new User();
    	        user.setName(data.name() + " " + data.lastName() );
    	        user.setEmail(data.email());
    	        user.setPassword(data.password());
    	        user.setTelephoneNumber("");
    	        user.setAddress("");
    			user.setDegreeType(User.DegreeType.UNIVERSITY_DEGREE);
    			user.setDegreeYear(0);
    			user.setPenalties(0);
    			user.setAverageRating(0.0);
    			user.setAdmin(false);
    			return user;
    			
    	                
    }
    
	private User userRecordToUser(UserRecord userDTO) {
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
}
