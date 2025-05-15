package com.student_loan;

import com.student_loan.model.Item;
import com.student_loan.model.Loan;
import com.student_loan.model.User;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.LoanRepository;
import com.student_loan.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
public class DataInitializer {
   @Bean
    CommandLineRunner initData(UserRepository userRepository, ItemRepository itemRepository, LoanRepository loanRepository) {
      return args -> {
         System.out.println("Initializing data...");
         List<User> users = createUsers();
         saveUsers(users, userRepository);
         
         List<Item> items = createItems(userRepository);
         saveItems(items, itemRepository);

         List<Loan> loans = createLoans(userRepository, itemRepository);
         saveLoans(loans, loanRepository);

         System.out.println("Data initialized successfully.");
      };
   }

   private List<User> createUsers() {
      List <User> users = new ArrayList<>();
   
      // Set of users to insert
      users.add(new User(null, "Ana García", "ana.garcia@email.com", "dF!94vH*2kQ#bR", "612 345 678", "Calle de Gran Vía, 23, 28013 Madrid", User.DegreeType.UNIVERSITY_DEGREE, 3, 0, 4.5, false));
      users.add(new User(null, "Carlos Pérez", "carlos.perez@email.com", "mA3!9bL2zR@xZ0", "633 111 222", "Carrer de Pau Claris, 46, 08010 Barcelona", User.DegreeType.MASTER, 2, 1, 4.2, false));
      users.add(new User(null, "Laura Fernández", "laura.fernandez@email.com", "lauraFSevilla", "622 333 444", "Avenida de la Constitución, 8, 41001 Sevilla", User.DegreeType.UNIVERSITY_DEGREE, 1, 0, 4.8, false));
      users.add(new User(null, "David López", "david.lopez@email.com", "qZ6!L@j*0eD9fT", "678 555 666", "Calle de Alcalá, 180, 28009 Madrid", User.DegreeType.DOCTORATE, 4, 0, 4.9, false));
      users.add(new User(null, "Marta Rodríguez", "marta.rodriguez@email.com", "yX5!8OqR2jW9hZ", "655 431 549", "Carrer de Roger de Llúria, 24, 08037 Barcelona", User.DegreeType.MASTER, 1, 0, 4.7, false));
      users.add(new User(null, "José Martínez", "jose.martinez@email.com", "deusto#00", "690 234 567", "Calle de Fuentecilla, 5, 14010 Córdoba", User.DegreeType.UNIVERSITY_DEGREE, 2, 0, 4.3, false));
      users.add(new User(null, "Raquel Sánchez", "raquel.sanchez@email.com", "rG5uT$7vI2gO#p1", "677 123 456", "Calle de la Paz, 34, 15001 A Coruña", User.DegreeType.UNIVERSITY_DEGREE, 3, 2, 3.8, false));
      users.add(new User(null, "José Antonio Torres", "jose.antonio@email.com", "uZ0!W6tP#3rV!fL", "616 741 982", "Calle San Fernando, 18, 41004 Sevilla", User.DegreeType.MASTER, 1, 0, 4.6, false));
      users.add(new User(null, "Beatriz García", "beatriz.garcia@email.com", "wQ2bF!xL5tCj9Z", "636 984 211", "Carrer de Valencia, 35, 08015 Barcelona", User.DegreeType.DOCTORATE, 3, 1, 4.4, false));
      users.add(new User(null, "Pedro Gómez", "pedro.gomez@email.com", "micontraseña", "688 234 123", "Calle Mayor, 12, 28013 Madrid", User.DegreeType.UNIVERSITY_DEGREE, 2, 0, 4.6, false));
      users.add(new User(null, "Carmen Martínez", "carmen.martinez@email.com", "vX0nP9!fS3zT1dQ", "676 453 782", "Avenida de la Paz, 40, 29012 Málaga", User.DegreeType.MASTER, 2, 1, 4.5, false));
      users.add(new User(null, "Antonio Fernández", "antonio.fernandez@email.com", "antoFernan01", "645 876 543", "Calle de Núñez de Balboa, 45, 28001 Madrid", User.DegreeType.UNIVERSITY_DEGREE, 3, 2, 4.7, false));
      users.add(new User(null, "Elena López", "elena.lopez@email.com", "qZ5#V7tJ3kL2A*1", "659 321 987", "Carrer de Sants, 58, 08014 Barcelona", User.DegreeType.MASTER, 1, 0, 4.8, false));
      users.add(new User(null, "Juan Pérez", "juan.perez@email.com", "kF3uT0hS7!pVbD2", "656 789 432", "Calle de Santiago, 14, 37001 Salamanca", User.DegreeType.DOCTORATE, 4, 0, 5.0, false));
      users.add(new User(null, "Sara García", "sara.garcia@email.com", "contra123", "602 123 654", "Calle del Sol, 22, 46001 Valencia", User.DegreeType.UNIVERSITY_DEGREE, 2, 0, 4.4, false));

      // Admin users
      users.add(new User(null, "Sabin Luja", "sabin.luja@opendeusto.es", "sabin", "602 123 654", "Hermanos Aguirre Kalea, 2, 48014 Bilbao", User.DegreeType.UNIVERSITY_DEGREE, 4, 0, 5.0, true));
      
      return users;
   }

   private void saveUsers(List<User> users, UserRepository userRepository) {
      for (User user : users) {
         if (userRepository.findByEmail(user.getEmail()) == null) {
               userRepository.save(user);
         }
      }
   }

   private List<Item> createItems(UserRepository userRepository) {
      // Get the ID of the User
      Long owner1Id = userRepository.findById(1L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner2Id = userRepository.findById(2L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner3Id = userRepository.findById(3L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner4Id = userRepository.findById(4L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner5Id = userRepository.findById(5L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner6Id = userRepository.findById(6L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner7Id = userRepository.findById(7L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner8Id = userRepository.findById(8L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner9Id = userRepository.findById(9L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner10Id = userRepository.findById(10L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner11Id = userRepository.findById(11L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner12Id = userRepository.findById(12L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner13Id = userRepository.findById(13L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner14Id = userRepository.findById(14L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long owner15Id = userRepository.findById(15L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));

      // Admins user ID
      Long admin00Id = userRepository.findById(16L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));

      // Set of items to insert
      List <Item> items = new ArrayList<>();
      items.add(new Item(null, "Artificial Intelligence Book", "Book on Artificial Intelligence for university students", "Books", Item.ItemStatus.BORROWED, owner1Id, new Date(122, 6, 15), 29.99, Item.ItemCondition.NEW, "/images/ArticialIntelligenceBook.jpg"));
      items.add(new Item(null, "HDMI 2.0 Cable", "Premium quality HDMI 2.0 cable for video and audio transmission", "Electronics", Item.ItemStatus.BORROWED, owner2Id, new Date(122, 9, 10), 15.00, Item.ItemCondition.VERY_USED, "/images/HDMI20Cable.jpg"));
      items.add(new Item(null, "Academic Planner", "A planner for organizing academic work and deadlines", "Stationery", Item.ItemStatus.BORROWED, owner3Id, new Date(122, 3, 5), 12.99, Item.ItemCondition.VERY_USED, "/images/AcademicPlanner.jpg"));
      items.add(new Item(null, "Graphing Scientific Calculator", "Advanced scientific calculator with graphing capabilities", "Calculators", Item.ItemStatus.AVAILABLE, owner4Id, new Date(122, 1, 20), 50.00, Item.ItemCondition.LIKE_NEW, "/images/GraphingScientificCalculator.jpg"));
      items.add(new Item(null, "Ergonomic Wireless Keyboard", "Ergonomically designed wireless keyboard for comfortable typing", "Electronics", Item.ItemStatus.AVAILABLE, owner5Id, new Date(122, 44, 12), 28.00, Item.ItemCondition.GOOD, "/images/ErgonomicWirelessKeyboard.jpg"));
      items.add(new Item(null, "Portable Bluetooth Speaker", "Compact Bluetooth speaker for clear sound anywhere", "Electronics", Item.ItemStatus.BORROWED, owner6Id, new Date(122, 5, 30), 40.00, Item.ItemCondition.NEW, "/images/PortableBluetoothSpeaker.jpg"));
      items.add(new Item(null, "Adjustable Smartphone Stand", "Multi-functional adjustable stand for smartphones and tablets", "Accessories", Item.ItemStatus.BORROWED, owner7Id, new Date(122, 10, 18), 12.00, Item.ItemCondition.USED, "/images/AdjustableSmartphoneStand.jpg"));
      items.add(new Item(null, "32GB USB Flash Drive", "USB flash drive with 32GB capacity for fast data transfer", "Electronics", Item.ItemStatus.UNAVAILABLE, owner8Id, new Date(122, 12, 22), 10.00, Item.ItemCondition.LIKE_NEW, "/images/32GBUSBFlashDrive.jpg"));
      items.add(new Item(null, "Compact Tool Kit", "Multi-purpose tool kit for small repairs and DIY projects", "Tools", Item.ItemStatus.AVAILABLE, owner9Id, new Date(123, 2, 8), 20.00, Item.ItemCondition.GOOD, "/images/CompactToolKit.jpg"));
      items.add(new Item(null, "Digital Drawing Tablet", "Graphics tablet with high precision for digital art and design", "Electronics", Item.ItemStatus.AVAILABLE, owner10Id, new Date(123, 12, 14), 150.00, Item.ItemCondition.VERY_USED, "/images/DigitalDrawingTablet.jpg"));
      items.add(new Item(null, "Introduction to Deep Learning", "Comprehensive textbook on Deep Learning concepts and applications for beginners and advanced students", "Books", Item.ItemStatus.AVAILABLE, owner11Id, new Date(123, 6, 3), 25.99, Item.ItemCondition.USED, "/images/IntroductiontoDeepLearning.jpg"));
      items.add(new Item(null, "Fast USB-C Charging Cable", "High-speed USB-C charger for fast charging of mobile devices", "Chargers", Item.ItemStatus.AVAILABLE, owner12Id, new Date(123, 6, 17), 10.50, Item.ItemCondition.VERY_USED, "/images/FastUSB-CChargingCable.jpg"));
      items.add(new Item(null, "Advanced Scientific Calculator", "Scientific calculator with advanced functions for university-level students", "Calculators", Item.ItemStatus.AVAILABLE, owner13Id, new Date(123, 9, 25), 15.00, Item.ItemCondition.GOOD, "/images/AdvancedScientificCalculator.jpg"));
      items.add(new Item(null, "Academic Student Notebook", "100-page notebook for note-taking and academic purposes", "Stationery", Item.ItemStatus.AVAILABLE, owner14Id, new Date(123, 10, 9), 2.50, Item.ItemCondition.NEW, "/images/AcademicStudentNotebook.jpg"));
      items.add(new Item(null, "Ergonomic Wireless Mouse", "Wireless ergonomic mouse designed for extended computer use", "Electronics", Item.ItemStatus.AVAILABLE, owner15Id, new Date(123, 1, 30), 12.00, Item.ItemCondition.LIKE_NEW, "/images/ErgonomicWirelessMouse.jpg"));
      items.add(new Item(null, "MSI Crosshair 16 HX Laptop", "Laptop with powerful specs for academic purposes, programming, and research", "Electronics", Item.ItemStatus.AVAILABLE, admin00Id, new Date(123, 4, 12), 500.00, Item.ItemCondition.NEW, "/images/MSICrosshair16HXLaptop.jpg"));
      items.add(new Item(null, "White-Out Correction Fluid", "High-quality white-out correction fluid for error-free corrections", "Stationery", Item.ItemStatus.AVAILABLE, owner2Id, new Date(123, 5, 20), 1.50, Item.ItemCondition.NEW, "/images/White-OutCorrectionFluid.jpg"));
      items.add(new Item(null, "Noise-Cancelling Headphones", "Over-ear headphones with noise-cancelling technology for immersive sound", "Electronics", Item.ItemStatus.AVAILABLE, owner3Id, new Date(124, 7, 11), 40.00, Item.ItemCondition.NEW, "/images/Noise-CancellingHeadphones.jpg"));
      items.add(new Item(null, "Precision Eraser", "Precision eraser for fine pencil erasures and clean work", "Stationery", Item.ItemStatus.AVAILABLE, owner4Id, new Date(1234, 12, 28), 0.50, Item.ItemCondition.NEW, "/images/PrecisionEraser.jpg"));
      items.add(new Item(null, "Algorithms Textbook", "In-depth textbook covering algorithms for computer science students", "Books", Item.ItemStatus.AVAILABLE, owner5Id, new Date(123, 1, 7), 35.00, Item.ItemCondition.USED, "/images/AlgorithmsTextbook.jpg"));
      items.add(new Item(null, "Arduino Development Kit", "Kit with Arduino board, sensors, and cables for electronics projects", "Electronics", Item.ItemStatus.BORROWED, owner6Id, new Date(123, 9, 15), 45.00, Item.ItemCondition.NEW, "/images/ArduinoDevelopmentKit.jpg"));
      items.add(new Item(null, "Full HD Webcam", "High-definition webcam ideal for video conferences and recordings", "Electronics", Item.ItemStatus.BORROWED, owner7Id, new Date(123, 8, 4), 35.00, Item.ItemCondition.LIKE_NEW, "/images/FullHDWebcam.jpg"));
      items.add(new Item(null, "1TB External Hard Drive", "Portable external hard drive for backups and data storage", "Electronics", Item.ItemStatus.AVAILABLE, owner8Id, new Date(124, 6, 19), 60.00, Item.ItemCondition.USED, "/images/1TBExternalHardDrive.jpg"));
      items.add(new Item(null, "Digital Multimeter", "Digital multimeter for measuring voltage, current, and resistance", "Tools", Item.ItemStatus.AVAILABLE, owner9Id, new Date(124, 1, 5), 25.00, Item.ItemCondition.NEW, "/images/DigitalMultimeter.jpg"));
      items.add(new Item(null, "Portable Printer", "Wireless portable printer for printing documents anywhere", "Electronics", Item.ItemStatus.BORROWED, owner10Id, new Date(124, 10, 22), 80.00, Item.ItemCondition.GOOD, "/images/PortablePrinter.jpg"));
      items.add(new Item(null, "Portable WiFi Router", "Portable WiFi router for creating wireless networks in areas without connection", "Electronics", Item.ItemStatus.AVAILABLE, owner11Id, new Date(124, 4, 14), 55.00, Item.ItemCondition.GOOD, "/images/PortableWiFiRouter.jpg"));
      items.add(new Item(null, "Rechargeable LED Desk Lamp", "LED desk lamp with rechargeable battery for studying at night", "Accessories", Item.ItemStatus.AVAILABLE, owner12Id, new Date(124, 6, 30), 20.00, Item.ItemCondition.USED, "/images/RechargeableLEDDeskLamp.jpg"));
      items.add(new Item(null, "Universal Laptop Charger", "Universal charger compatible with multiple laptop models", "Electronics", Item.ItemStatus.AVAILABLE, owner13Id, new Date(124, 8, 8), 30.00, Item.ItemCondition.LIKE_NEW, "/images/UniversalLaptopCharger.jpg"));
      items.add(new Item(null, "Raspberry Pi Board", "Raspberry Pi development board for programming and electronics projects", "Electronics", Item.ItemStatus.AVAILABLE, owner14Id, new Date(124, 3, 16), 35.00, Item.ItemCondition.USED, "/images/RaspberryPiBoard.jpg"));
      items.add(new Item(null, "USB Digital Microscope", "Portable USB digital microscope for detailed analysis and studies", "Electronics", Item.ItemStatus.AVAILABLE, owner15Id, new Date(124, 11, 27), 70.00, Item.ItemCondition.GOOD, "/images/USBDigitalMicroscope.jpg"));
      items.add(new Item(null, "Calculus Textbook", "A comprehensive textbook covering calculus concepts for university students", "Books", Item.ItemStatus.UNAVAILABLE, owner1Id, new Date(124, 0, 12), 55.00, Item.ItemCondition.USED, "/images/CalculusTextbook.jpg"));
      items.add(new Item(null, "Campus Umbrella", "Durable umbrella to shield students from rain on campus", "Accessories", Item.ItemStatus.BORROWED, owner2Id, new Date(124, 1, 20), 20.00, Item.ItemCondition.GOOD, "/images/CampusUmbrella.jpg"));
      items.add(new Item(null, "Wireless Earbuds", "Compact and high-quality wireless earbuds for everyday use", "Electronics", Item.ItemStatus.BORROWED, owner3Id, new Date(124, 7, 5), 35.00, Item.ItemCondition.GOOD, "/images/WirelessEarbuds.jpg"));
      items.add(new Item(null, "Laptop Cooling Pad", "Cooling pad to enhance laptop performance during heavy use", "Electronics", Item.ItemStatus.AVAILABLE, owner4Id, new Date(124, 3, 18), 25.00, Item.ItemCondition.VERY_USED, "/images/LaptopCoolingPad.jpg"));
      items.add(new Item(null, "Multiport USB Hub", "USB hub with multiple ports to connect several devices simultaneously", "Electronics", Item.ItemStatus.AVAILABLE, owner5Id, new Date(124, 11, 2), 18.00, Item.ItemCondition.NEW, "/images/MultiportUSBHub.jpg"));
      items.add(new Item(null, "Stainless Steel Thermos", "Insulated thermos to keep beverages hot or cold for long hours", "Accessories", Item.ItemStatus.AVAILABLE, owner6Id, new Date(124, 10, 25), 30.00, Item.ItemCondition.VERY_USED, "/images/StainlessSteelThermos.jpg"));
      items.add(new Item(null, "Bluetooth Presenter Remote", "Remote control for presentations with Bluetooth connectivity", "Electronics", Item.ItemStatus.AVAILABLE, owner7Id, new Date(124, 8, 14), 22.00, Item.ItemCondition.USED, "/images/BluetoothPresenterRemote.jpg"));
      items.add(new Item(null, "Desk Lamp", "LED desk lamp with adjustable brightness for late-night studies", "Furniture", Item.ItemStatus.AVAILABLE, owner8Id, new Date(124, 5, 30), 40.00, Item.ItemCondition.NEW, "/images/DeskLamp.jpg"));
      items.add(new Item(null, "USB 16GB", "portable USB for quick data transfer storage or presentations", "Electronics", Item.ItemStatus.AVAILABLE, owner9Id, new Date(124, 4, 7), 90.00, Item.ItemCondition.USED, "/images/USB16GB.jpg"));
      items.add(new Item(null, "Advanced English Dictionary", "Comprehensive dictionary for academic writing and research", "Books", Item.ItemStatus.AVAILABLE, admin00Id, new Date(124, 2, 3), 40.00, Item.ItemCondition.NEW, "/images/AdvancedEnglishDictionary.jpg"));
      items.add(new Item(null, "Basic Pencil Case", "Durable pencil case to store pens and pencils", "Stationery", Item.ItemStatus.AVAILABLE, owner1Id, new Date(125, 2, 14), 8.50, Item.ItemCondition.NEW, "/images/BasicPencilCase.jpg"));
      items.add(new Item(null, "Ballpoint Pens Set", "Set of high-quality ballpoint pens for everyday writing", "Stationery", Item.ItemStatus.AVAILABLE, owner2Id, new Date(125, 0, 10), 5.00, Item.ItemCondition.VERY_USED, "/images/BallpointPensSet.jpg"));
      items.add(new Item(null, "Scientific Calculator", "Reliable scientific calculator for complex calculations", "Calculators", Item.ItemStatus.AVAILABLE, owner3Id, new Date(125, 1, 25), 25.00, Item.ItemCondition.USED, "/images/ScientificCalculator.jpg"));
      items.add(new Item(null, "Artificial Intelligence: A Modern Approach", "Comprehensive textbook covering modern AI concepts", "Books", Item.ItemStatus.AVAILABLE, owner4Id, new Date(125, 3, 5), 60.00, Item.ItemCondition.NEW, "/images/ArtificialIntelligenceAModernApproach.jpg"));
      items.add(new Item(null, "Introduction to Computer Science", "Fundamental textbook on computer science principles", "Books", Item.ItemStatus.AVAILABLE, owner5Id, new Date(125, 2, 15), 55.00, Item.ItemCondition.NEW, "/images/IntroductiontoComputerScience.jpg"));
      items.add(new Item(null, "Introduction to Business Administration (ADE)", "Fundamentals of Business Administration for university students", "Books", Item.ItemStatus.AVAILABLE, owner1Id, new Date(125, 0, 15), 40.00, Item.ItemCondition.USED, "/images/IntroductiontoBusinessAdministration.jpg"));
      items.add(new Item(null, "Natural Language Processing with Python", "Comprehensive guide to NLP techniques using Python for text analysis", "Books", Item.ItemStatus.AVAILABLE, owner2Id, new Date(125,2, 2), 45.00, Item.ItemCondition.NEW, "/images/NaturalLanguageProcessingwithPython.jpg"));
      items.add(new Item(null, "Reinforcement Learning: An Introduction", "A detailed introduction to reinforcement learning algorithms and applications", "Books", Item.ItemStatus.AVAILABLE, owner3Id, new Date(125, 1, 14), 50.00, Item.ItemCondition.NEW, "/images/ReinforcementLearningAnIntroduction.jpg"));
      items.add(new Item(null, "Ethical Hacking: Practical Guide", "A practical guide to ethical hacking and penetration testing", "Books", Item.ItemStatus.AVAILABLE, owner4Id, new Date(125, 2, 9), 38.00, Item.ItemCondition.NEW, "/images/EthicalHackingPracticalGuide.jpg"));
      items.add(new Item(null, "Information Systems: Theory and Practice", "Comprehensive textbook covering information systems concepts and applications", "Books", Item.ItemStatus.AVAILABLE, owner5Id, new Date(125, 3, 1), 42.00, Item.ItemCondition.NEW, "/images/InformationSystemsTheoryandPractice.jpg"));
      
      return items;
   }

   private void saveItems(List<Item> items, ItemRepository itemRepository) {
      for (Item item : items) {
         if (itemRepository.findByName(item.getName()) == null) {
               itemRepository.save(item);
         }
      }
   }

   private List<Loan> createLoans(UserRepository userRepository, ItemRepository itemRepository) {
      // Get the ID of the User
      Long user_loan1 = userRepository.findById(1L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long user_loan2 = userRepository.findById(2L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long user_loan3 = userRepository.findById(3L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long user_loan4 = userRepository.findById(4L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long user_loan5 = userRepository.findById(5L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long user_loan6 = userRepository.findById(6L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long user_loan7 = userRepository.findById(7L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long user_loan8 = userRepository.findById(8L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long user_loan9 = userRepository.findById(9L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
      Long user_loan10 = userRepository.findById(10L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));

      // Get the ID of the Item
      Long item_loan1 = itemRepository.findById(1L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan2 = itemRepository.findById(2L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan3 = itemRepository.findById(3L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan4 = itemRepository.findById(4L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan5 = itemRepository.findById(5L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan6 = itemRepository.findById(6L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan7 = itemRepository.findById(7L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan8 = itemRepository.findById(8L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan9 = itemRepository.findById(9L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan20 = itemRepository.findById(10L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan21 = itemRepository.findById(21L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan22 = itemRepository.findById(22L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan23 = itemRepository.findById(23L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan24 = itemRepository.findById(24L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan25 = itemRepository.findById(25L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan31 = itemRepository.findById(31L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan32 = itemRepository.findById(32L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan33 = itemRepository.findById(33L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan34 = itemRepository.findById(34L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));
      Long item_loan35 = itemRepository.findById(35L).map(Item::getId).orElseThrow(() -> new RuntimeException("Item not found"));

      // Set of loans to insert
      List <Loan> loans = new ArrayList<>();
      loans.add(new Loan(null, user_loan1, user_loan2, item_loan1, new Date(125, 1, 5), new Date(125, 43, 19), null, Loan.Status.IN_USE, null, ""));
      loans.add(new Loan(null, user_loan2, user_loan4, item_loan2, new Date(125, 2, 8), new Date(125, 4, 15), null, Loan.Status.IN_USE, null, ""));
      loans.add(new Loan(null, user_loan3, user_loan6, item_loan3, new Date(125, 0, 18), new Date(125, 3, 30), null, Loan.Status.IN_USE, null, ""));
      loans.add(new Loan(null, user_loan4, user_loan8, item_loan4, new Date(125, 1, 12), new Date(125, 1, 20), new Date(125, 1, 20), Loan.Status.RETURNED, 5.0, "Excellent"));
      loans.add(new Loan(null, user_loan5, user_loan10, item_loan5, new Date(125, 1, 28), new Date(125, 2, 7), new Date(125, 2, 6), Loan.Status.RETURNED, 4.8, "Returned early"));
      loans.add(new Loan(null, user_loan6, user_loan9, item_loan6, new Date(125, 3, 1), new Date(125, 4, 15), null, Loan.Status.IN_USE, null, ""));
      loans.add(new Loan(null, user_loan7, user_loan7, item_loan7, new Date(125, 3, 10), new Date(125, 5, 17), null, Loan.Status.IN_USE, null, ""));
      loans.add(new Loan(null, user_loan8, user_loan6, item_loan8, new Date(125, 2, 8), new Date(125, 2, 22), null, Loan.Status.LOST, null, "Item lost during loan"));
      loans.add(new Loan(null, user_loan9, user_loan1, item_loan9, new Date(125, 0, 10), new Date(125, 0, 18), new Date(125, 0, 19), Loan.Status.RETURNED, 4.9, "Returned with care"));
      loans.add(new Loan(null, user_loan5, user_loan3, item_loan20, new Date(125, 2, 3), new Date(125, 2, 10), new Date(125, 2, 12), Loan.Status.RETURNED, 4.2, "Slight delay"));
      loans.add(new Loan(null, user_loan6, user_loan5, item_loan21, new Date(125, 3, 1), new Date(125, 3, 10), null, Loan.Status.IN_USE, null, ""));
      loans.add(new Loan(null, user_loan7, user_loan4, item_loan22, new Date(125, 2, 15), new Date(125, 3, 22), null, Loan.Status.IN_USE, null, ""));
      loans.add(new Loan(null, user_loan8, user_loan2, item_loan23, new Date(125, 1, 5), new Date(125, 1, 12), new Date(125, 1, 11), Loan.Status.RETURNED, 4.6, "Early return"));
      loans.add(new Loan(null, user_loan9, user_loan6, item_loan24, new Date(125, 0, 10), new Date(125, 0, 20), new Date(125, 0, 18), Loan.Status.RETURNED, 4.8, "Returned ahead of time"));
      loans.add(new Loan(null, user_loan10, user_loan8, item_loan25, new Date(125, 1, 14), new Date(125, 2, 25), null, Loan.Status.IN_USE, null, ""));
      loans.add(new Loan(null, user_loan1, user_loan1, item_loan31, new Date(125, 2, 3), new Date(125, 2, 10), null, Loan.Status.LOST, null, "Item lost during loan"));
      loans.add(new Loan(null, user_loan2, user_loan7, item_loan32, new Date(125, 3, 2), new Date(125, 3, 30), null, Loan.Status.IN_USE, null, ""));
      loans.add(new Loan(null, user_loan3, user_loan9, item_loan33, new Date(125, 2, 17), new Date(125, 3, 24), null, Loan.Status.IN_USE, null, ""));
      loans.add(new Loan(null, user_loan4, user_loan10, item_loan34, new Date(125, 2, 11), new Date(125, 2, 20), new Date(125, 2, 20), Loan.Status.RETURNED, 5.0, "Excellent return"));
      loans.add(new Loan(null, user_loan5, user_loan5, item_loan35, new Date(125, 1, 1), new Date(125, 1, 14), new Date(125, 1, 13), Loan.Status.RETURNED, 4.2, "Returned without issues"));
      
      return loans;
   }

   private void saveLoans(List<Loan> loans, LoanRepository loanRepository) {
      for (Loan loan : loans) {
         List<Loan> itemLoans = loanRepository.findByItem(loan.getItem());
          
         boolean hasActiveLoan = false;
         for (Loan loan2 : itemLoans) {
            if (loan2.getLoanStatus() != Loan.Status.RETURNED) {
               hasActiveLoan = true;
               break;
            }
         }
  
         if (!hasActiveLoan) {
            loanRepository.save(loan);
         }
      }
   }   
}
