package com.student_loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.student_loan.model.User;
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
}
