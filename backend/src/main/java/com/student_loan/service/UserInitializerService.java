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

    private boolean lanzado = true;

    @PostConstruct
    @Transactional
    public void insertDefaultUsers() {
        
        if (lanzado) {
            System.out.println("Users are already in the database.");
            
        } else {
             // Set of users to insert
            User user1 = new User(null, "Ana García", "ana.garcia@email.com", "dF!94vH*2kQ#bR", "612 345 678", "Calle de Gran Vía, 23, 28013 Madrid", User.DegreeType.UNIVERSITY_DEGREE, 3, 0, 4.5, false);
            User user2 = new User(null, "Carlos Pérez", "carlos.perez@email.com", "mA3!9bL2zR@xZ0", "633 111 222", "Carrer de Pau Claris, 46, 08010 Barcelona", User.DegreeType.MASTER, 2, 1, 4.2, false);
            User user3 = new User(null, "Laura Fernández", "laura.fernandez@email.com", "lauraFSevilla", "622 333 444", "Avenida de la Constitución, 8, 41001 Sevilla", User.DegreeType.UNIVERSITY_DEGREE, 1, 0, 4.8, false);
            User user4 = new User(null, "David López", "david.lopez@email.com", "qZ6!L@j*0eD9fT", "678 555 666", "Calle de Alcalá, 180, 28009 Madrid", User.DegreeType.DOCTORATE, 4, 0, 4.9, false);
            User user5 = new User(null, "Marta Rodríguez", "marta.rodriguez@email.com", "yX5!8OqR2jW9hZ", "655 431 549", "Carrer de Roger de Llúria, 24, 08037 Barcelona", User.DegreeType.MASTER, 1, 0, 4.7, false);
            User user6 = new User(null, "José Martínez", "jose.martinez@email.com", "deusto#00", "690 234 567", "Calle de Fuentecilla, 5, 14010 Córdoba", User.DegreeType.UNIVERSITY_DEGREE, 2, 0, 4.3, false);
            User user7 = new User(null, "Raquel Sánchez", "raquel.sanchez@email.com", "rG5uT$7vI2gO#p1", "677 123 456", "Calle de la Paz, 34, 15001 A Coruña", User.DegreeType.UNIVERSITY_DEGREE, 3, 2, 3.8, false);
            User user8 = new User(null, "José Antonio Torres", "jose.antonio@email.com", "uZ0!W6tP#3rV!fL", "616 741 982", "Calle San Fernando, 18, 41004 Sevilla", User.DegreeType.MASTER, 1, 0, 4.6, false);
            User user9 = new User(null, "Beatriz García", "beatriz.garcia@email.com", "wQ2bF!xL5tCj9Z", "636 984 211", "Carrer de Valencia, 35, 08015 Barcelona", User.DegreeType.DOCTORATE, 3, 1, 4.4, false);
            User user10 = new User(null, "Pedro Gómez", "pedro.gomez@email.com", "micontraseña", "688 234 123", "Calle Mayor, 12, 28013 Madrid", User.DegreeType.UNIVERSITY_DEGREE, 2, 0, 4.6, false);
            User user11 = new User(null, "Carmen Martínez", "carmen.martinez@email.com", "vX0nP9!fS3zT1dQ", "676 453 782", "Avenida de la Paz, 40, 29012 Málaga", User.DegreeType.MASTER, 2, 1, 4.5, false);
            User user12 = new User(null, "Antonio Fernández", "antonio.fernandez@email.com", "antoFernan01", "645 876 543", "Calle de Núñez de Balboa, 45, 28001 Madrid", User.DegreeType.UNIVERSITY_DEGREE, 3, 2, 4.7, false);
            User user13 = new User(null, "Elena López", "elena.lopez@email.com", "qZ5#V7tJ3kL2A*1", "659 321 987", "Carrer de Sants, 58, 08014 Barcelona", User.DegreeType.MASTER, 1, 0, 4.8, false);
            User user14 = new User(null, "Juan Pérez", "juan.perez@email.com", "kF3uT0hS7!pVbD2", "656 789 432", "Calle de Santiago, 14, 37001 Salamanca", User.DegreeType.DOCTORATE, 4, 0, 5.0, false);
            User user15 = new User(null, "Sara García", "sara.garcia@email.com", "contra123", "602 123 654", "Calle del Sol, 22, 46001 Valencia", User.DegreeType.UNIVERSITY_DEGREE, 2, 0, 4.4, false);

            User admin00 = new User(null, "Sabin Luja", "sabin.luja@opendeusto.es", "sabin", "602 123 654", "Hermanos Aguirre Kalea, 2, 48014 Bilbao", User.DegreeType.UNIVERSITY_DEGREE, 4, 0, 5.0, true);

            // Save users to the database
            userRepository.save(user1); userRepository.save(user2); userRepository.save(user3); userRepository.save(user4); userRepository.save(user5);
            userRepository.save(user6); userRepository.save(user7); userRepository.save(user8); userRepository.save(user9); userRepository.save(user10);
            userRepository.save(user11); userRepository.save(user12); userRepository.save(user13); userRepository.save(user14); userRepository.save(user15);

            userRepository.save(admin00);

            // Fetch their IDs
            Long owner1Id = user1.getId(); Long owner2Id = user2.getId(); Long owner3Id = user3.getId(); Long owner4Id = user4.getId(); Long owner5Id = user5.getId();
            Long owner6Id = user6.getId(); Long owner7Id = user7.getId(); Long owner8Id = user8.getId(); Long owner9Id = user9.getId(); Long owner10Id = user10.getId();
            Long owner11Id = user11.getId();Long owner12Id = user12.getId(); Long owner13Id = user13.getId(); Long owner14Id = user14.getId(); Long owner15Id = user15.getId();

            Long admin00Id = admin00.getId();

            System.out.println("Users inserted successfully.");
        }
    }
}
