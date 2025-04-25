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

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private Map<String, User> tokens = new HashMap<>();

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil = new JwtUtil();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User updatedUserData) {
        Optional<User> existingUserOptional = userRepository.findById(id);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            // Update only the fields that are not null in the updatedUserData
            String newName = updatedUserData.getName() == null ? existingUser.getName() : updatedUserData.getName();
            existingUser.setName(newName);
            String newEmail = updatedUserData.getEmail() == null ? existingUser.getEmail() : updatedUserData.getEmail();
            existingUser.setEmail(newEmail);
            String newTelephoneNumber = updatedUserData.getTelephoneNumber() == null ? existingUser.getTelephoneNumber() : updatedUserData.getTelephoneNumber();
            existingUser.setTelephoneNumber(newTelephoneNumber);
            String newAddress = updatedUserData.getAddress() == null ? existingUser.getAddress() : updatedUserData.getAddress();
            existingUser.setAddress(newAddress);
            String newpassword = updatedUserData.getPassword() == null ? existingUser.getPassword() : updatedUserData.getPassword();
            existingUser.setPassword(newpassword);
            DegreeType newDegreeType = updatedUserData.getDegreeType() == null ? existingUser.getDegreeType() : updatedUserData.getDegreeType();
            existingUser.setDegreeType(newDegreeType);
            Integer newDegreeYear = updatedUserData.getDegreeYear() == null ? existingUser.getDegreeYear() : updatedUserData.getDegreeYear();
            existingUser.setDegreeYear(newDegreeYear);
            Integer newPenalties = updatedUserData.getPenalties() == null ? existingUser.getPenalties() : updatedUserData.getPenalties();
            existingUser.setPenalties(newPenalties);
            Double newAverageRating = updatedUserData.getAverageRating() == null ? existingUser.getAverageRating() : updatedUserData.getAverageRating();
            existingUser.setAverageRating(newAverageRating);
            Boolean newAdmin = updatedUserData.getAdmin() == null ? existingUser.getAdmin() : updatedUserData.getAdmin();
            existingUser.setAdmin(newAdmin);
            

            if (updatedUserData.getPassword() != null && !updatedUserData.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUserData.getPassword()));
            }
            
            return userRepository.save(existingUser);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }



    public boolean register(User user) {
        if(userRepository.findByEmail(user.getEmail())!=null) {
    		return false;
    	}else {
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypts the password
            userRepository.save(user);
			return true;
    	}
    }

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

	public boolean logout(String token) {
		if(tokens.containsKey(token)) {
            tokens.remove(token);
            return true;
        }else{
        	return false;}
	}
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
	public User getUserByToken(String token) {
		return tokens.get(token);
	}

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
