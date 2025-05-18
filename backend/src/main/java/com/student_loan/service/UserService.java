package com.student_loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.student_loan.model.User;
import com.student_loan.model.User.DegreeType;
import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.student_loan.security.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for managing users in the system. Handles operations such as
 * retrieving, creating, saving, and deleting users.
 */
@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    
    @Autowired
    private JwtUtil jwtUtil;

    private Map<String, User> tokens = new HashMap<>();

	/**
	 * Retrieves all users from the repository.
	 *
	 * @return List of all users.
	 */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

        /**
         * Finds a user by their ID.	
         * @param id
         * @return
         */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Updates a user's information.
     *
     * @param id The ID of the user to update.
     * @param updatedUserData Object containing updated fields.
     * @return The updated user.
     * @throws RuntimeException if user is not found.
     */
    public User updateUser(Long id, User newData) {
        User existing = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (newData.getName() != null) {
            existing.setName(newData.getName());
        }
        if (newData.getEmail() != null) {
            existing.setEmail(newData.getEmail());
        }
        if (newData.getTelephoneNumber() != null) {
            existing.setTelephoneNumber(newData.getTelephoneNumber());
        }
        if (newData.getAddress() != null) {
            existing.setAddress(newData.getAddress());
        }
        if (newData.getDegreeType() != null) {
            existing.setDegreeType(newData.getDegreeType());
        }
        if (newData.getDegreeYear() != null) {
            existing.setDegreeYear(newData.getDegreeYear());
        }
        if (newData.getAverageRating() != null) {
            existing.setAverageRating(newData.getAverageRating());
        }

        String rawPass = newData.getPassword();
        if (rawPass != null && !rawPass.isBlank()) {
            existing.setPassword(passwordEncoder.encode(rawPass));
        }

        Integer previousPenalties = existing.getPenalties();
        Integer newPenalties      = newData.getPenalties();
        
        if (newPenalties != null) {
            if (newPenalties > previousPenalties) {
                notificationService.enviarCorreo(
                    existing.getEmail(),
                    "NEW PENALTY!",
                    "Your penalty count increased to " + newPenalties
                );
            }
            existing.setPenalties(newPenalties);
        } else {
            existing.setPenalties(previousPenalties);
        }
        existing.setAdmin(newData.getAdmin());

        return userRepository.save(existing);
    }


    /**
     * Registers a new user after checking for email uniqueness.
     *
     * @param user The user to register.
     * @return true if registration is successful, false if email already exists.
     */
    public boolean register(User user) {
        if(userRepository.findByEmail(user.getEmail())!=null) {
    		return false;
    	}else {
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypts the password
            userRepository.save(user);
			return true;
    	}
    }

    
    /**
     * Authenticates a user using email and password.
     *
     * @param credentials Login credentials.
     * @return JWT token if successful, or error message.
     */
    public String login(CredentialsDTO credentials) {
        User user = userRepository.findByEmail(credentials.getEmail());
        if (tokens.containsValue(user)) {
            return "User already logged in";
        } else if (user != null && passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(credentials.getEmail());
            tokens.put(token, user);
            return token;
        } else {
            return "Invalid credentials";
        }
    }
    
    /**
     * Logs out a user by invalidating their token.
     *
     * @param token The user's token.
     * @return true if logout is successful, false otherwise.
     */

	public boolean logout(String token) {
		if(tokens.containsKey(token)) {
            tokens.remove(token);
            return true;
        }else{
        	return false;}
	}
    
	 /**
     * Deletes a user by ID.
     *
     * @param id User ID.
     * @throws RuntimeException if the user is not found.
     */
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found");
        }
    }
    /**
     * Retrieves a user based on a valid JWT token.
     *
     * @param token JWT token.
     * @return The user associated with the token.
     */
	public User getUserByToken(String token) {
		return tokens.get(token);
	}

	  /**
     * Finds a user by their email address.
     *
     * @param email The user's email.
     * @return The user if found, or null otherwise.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
