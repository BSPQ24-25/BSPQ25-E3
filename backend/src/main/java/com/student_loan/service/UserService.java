package com.student_loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.student_loan.model.User;
import com.student_loan.dtos.CredentialsDTO;
import com.student_loan.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private Map<String, User> tokens = new HashMap<>();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public boolean register(User user) {
    	if(userRepository.findByEmail(user.getEmail())!=null) {
    		return false;
    	}else {
			userRepository.save(user);
			return true;
    	}
    }
    
    public String login(CredentialsDTO credentials) {
    	User userDB = userRepository.findByEmail(credentials.getEmail());
		if (tokens.containsValue(userDB)) {
			return "User already logged in";
		}	else if(userDB != null && userDB.getPassword().equals(credentials.getPassword())) {
			String token = generateToken();
			tokens.put(token, userDB);
			return token;
    	}else {
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
    
    
    private static synchronized String generateToken() {
        return Long.toString(System.currentTimeMillis());
    }
    
}
