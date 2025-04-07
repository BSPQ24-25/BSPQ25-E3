package com.student_loan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.student_loan.model.User;
import com.student_loan.service.UserService;
import com.student_loan.dtos.UserDTO;
import com.student_loan.dtos.CredentialsDTO;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
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
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    //Register 
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        if (userService.register(dtoToUser(userDTO))) {
            return ResponseEntity.ok("User registered correctly");
        } else {
            return ResponseEntity.badRequest().body("The user already exists");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
    
	private User dtoToUser(UserDTO userDTO) {
		User user = new User();
		user.setName(userDTO.getName()+" "+userDTO.getLastName());
		user.setEmail(userDTO.getEmail());
		user.setPassword(userDTO.getPassword());
		user.setTelephoneNumber("");
		user.setAddress("");
		user.setDegreeType(User.DegreeType.UNIVERSITY_DEGREE);
		user.setDegreeYear(0);
		user.setPenalties(0);
		user.setAverageRating(0.0);
		
		return user;
	}
}
