package com.student_loan.service;

import com.student_loan.model.User;
import com.student_loan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import jakarta.annotation.PostConstruct;

@Service
public class UserInitializerService {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    @Transactional
    public void insertDefaultUsers() {
        // Set of users to insert
        User user1 = new User(null, "Alice Johnson", "alice.johnson@email.com", "password123", "617 037 582", "123 Maple St, City, Country", User.DegreeType.UNIVERSITY_DEGREE, 3, 0, 4.5, false);
        User user2 = new User(null, "Bob Smith", "bob.smith@email.com", "password456", "619 491 459", "234 Oak Rd, City, Country", User.DegreeType.MASTER, 2, 1, 4.2, false);
        User user3 = new User(null, "Charlie Davis", "charlie.davis@email.com", "password789", "621 003 576", "345 Pine Ave, City, Country", User.DegreeType.UNIVERSITY_DEGREE, 1, 0, 4.8, false);
        User user4 = new User(null, "Diana Wilson", "diana.wilson@email.com", "password101", "6183 435 364", "456 Birch Blvd, City, Country", User.DegreeType.DOCTORATE, 4, 0, 4.9, true);
        User user5 = new User(null, "Eve Martinez", "eve.martinez@email.com", "password202", "655 431 549", "567 Cedar Ln, City, Country", User.DegreeType.MASTER, 1, 0, 4.7, false);
        User user6 = new User(null, "Frank Moore", "frank.moore@email.com", "password303", "600 178 138", "678 Elm St, City, Country", User.DegreeType.UNIVERSITY_DEGREE, 2, 0, 4.3, false);
        User user7 = new User(null, "Grace Lee", "grace.lee@email.com", "password404", "666 431 453", "789 Willow Way, City, Country", User.DegreeType.UNIVERSITY_DEGREE, 3, 2, 3.8, false);
        User user8 = new User(null, "Hank Taylor", "hank.taylor@email.com", "password505", "614 547 901", "890 Fir Dr, City, Country", User.DegreeType.MASTER, 1, 0, 4.6, false);
        User user9 = new User(null, "Ivy Clark", "ivy.clark@email.com", "password606", "607 789 204", "901 Ash Blvd, City, Country", User.DegreeType.DOCTORATE, 3, 1, 4.4, true);
        User user10 = new User(null, "Jackie King", "jackie.king@email.com", "password707", "678 478 928", "1010 Redwood Ave, City, Country", User.DegreeType.UNIVERSITY_DEGREE, 2, 0, 4.6, false);

        // Save users to the database
        userRepository.save(user1); userRepository.save(user2); userRepository.save(user3); userRepository.save(user4); userRepository.save(user5);
        userRepository.save(user6); userRepository.save(user7); userRepository.save(user8); userRepository.save(user9); userRepository.save(user10);

        // Fetch their IDs
        Long owner1Id = user1.getId();
        Long owner2Id = user2.getId();
        Long owner3Id = user3.getId();
        Long owner4Id = user4.getId();
        Long owner5Id = user5.getId();
        Long owner6Id = user6.getId();
        Long owner7Id = user7.getId();
        Long owner8Id = user8.getId();
        Long owner9Id = user9.getId();
        Long owner10Id = user10.getId();

        System.out.println("Users inserted successfully.");
    }
}
