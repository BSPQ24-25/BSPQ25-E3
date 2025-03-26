package com.student_loan.service;

import com.student_loan.model.Item;
import com.student_loan.model.User;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import jakarta.annotation.PostConstruct;
import java.util.Date;

@Service
public class ItemInitializerService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private boolean lanzado = true;

    @PostConstruct
    @Transactional
    public void insertDefaultItems() {

        if (lanzado) {
            System.out.println("Items are already in the database.");
            
        } else {

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

            Long admin00Id = userRepository.findById(16L).map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));

            // Set of items to insert
            Item item1 = new Item(null, "Artificial Intelligence Book", "Book on Artificial Intelligence for university students", "Books", Item.ItemStatus.BORROWED, owner1Id, new Date(122, 6, 15), 29.99, Item.ItemCondition.NEW, "ai_book.jpg");
            Item item2 = new Item(null, "HDMI 2.0 Cable", "Premium quality HDMI 2.0 cable for video and audio transmission", "Electronics", Item.ItemStatus.BORROWED, owner2Id, new Date(122, 9, 10), 15.00, Item.ItemCondition.VERY_USED, "hdmi_2_0_cable.jpg");
            Item item3 = new Item(null, "Academic Planner", "A planner for organizing academic work and deadlines", "Stationery", Item.ItemStatus.BORROWED, owner3Id, new Date(122, 3, 5), 12.99, Item.ItemCondition.VERY_USED, "academic_planner.jpg");
            Item item4 = new Item(null, "Graphing Scientific Calculator", "Advanced scientific calculator with graphing capabilities", "Calculators", Item.ItemStatus.AVAILABLE, owner4Id, new Date(122, 1, 20), 50.00, Item.ItemCondition.LIKE_NEW, "graphing_scientific_calculator.jpg");
            Item item5 = new Item(null, "Ergonomic Wireless Keyboard", "Ergonomically designed wireless keyboard for comfortable typing", "Electronics", Item.ItemStatus.AVAILABLE, owner5Id, new Date(122, 44, 12), 28.00, Item.ItemCondition.GOOD, "ergonomic_wireless_keyboard.jpg");
            Item item6 = new Item(null, "Portable Bluetooth Speaker", "Compact Bluetooth speaker for clear sound anywhere", "Electronics", Item.ItemStatus.BORROWED, owner6Id, new Date(122, 5, 30), 40.00, Item.ItemCondition.NEW, "portable_bluetooth_speaker.jpg");
            Item item7 = new Item(null, "Adjustable Smartphone Stand", "Multi-functional adjustable stand for smartphones and tablets", "Accessories", Item.ItemStatus.BORROWED, owner7Id, new Date(122, 10, 18), 12.00, Item.ItemCondition.USED, "adjustable_smartphone_stand.jpg");
            Item item8 = new Item(null, "32GB USB Flash Drive", "USB flash drive with 32GB capacity for fast data transfer", "Electronics", Item.ItemStatus.UNAVAILABLE, owner8Id, new Date(122, 12, 22), 10.00, Item.ItemCondition.LIKE_NEW, "32gb_usb_flash_drive.jpg");
            Item item9 = new Item(null, "Compact Tool Kit", "Multi-purpose tool kit for small repairs and DIY projects", "Tools", Item.ItemStatus.AVAILABLE, owner9Id, new Date(123, 2, 8), 20.00, Item.ItemCondition.GOOD, "compact_tool_kit.jpg");
            Item item10 = new Item(null, "Digital Drawing Tablet", "Graphics tablet with high precision for digital art and design", "Electronics", Item.ItemStatus.AVAILABLE, owner10Id, new Date(123, 12, 14), 150.00, Item.ItemCondition.VERY_USED, "digital_drawing_tablet.jpg");
            Item item11 = new Item(null, "Introduction to Deep Learning", "Comprehensive textbook on Deep Learning concepts and applications for beginners and advanced students", "Books", Item.ItemStatus.AVAILABLE, owner11Id, new Date(123, 6, 3), 25.99, Item.ItemCondition.USED, "ai_book.jpg");
            Item item12 = new Item(null, "Fast USB-C Charging Cable", "High-speed USB-C charger for fast charging of mobile devices", "Chargers", Item.ItemStatus.AVAILABLE, owner12Id, new Date(123, 6, 17), 10.50, Item.ItemCondition.VERY_USED, "charger_type_c.jpg");
            Item item13 = new Item(null, "Advanced Scientific Calculator", "Scientific calculator with advanced functions for university-level students", "Calculators", Item.ItemStatus.AVAILABLE, owner13Id, new Date(123, 9, 25), 15.00, Item.ItemCondition.GOOD, "calculator.jpg");
            Item item14 = new Item(null, "Academic Student Notebook", "100-page notebook for note-taking and academic purposes", "Stationery", Item.ItemStatus.AVAILABLE, owner14Id, new Date(123, 10, 9), 2.50, Item.ItemCondition.NEW, "notebook.jpg");
            Item item15 = new Item(null, "Ergonomic Wireless Mouse", "Wireless ergonomic mouse designed for extended computer use", "Electronics", Item.ItemStatus.AVAILABLE, owner15Id, new Date(123, 1, 30), 12.00, Item.ItemCondition.LIKE_NEW, "wireless_mouse.jpg");
            Item item16 = new Item(null, "MSI Crosshair 16 HX Laptop", "Laptop with powerful specs for academic purposes, programming, and research", "Electronics", Item.ItemStatus.AVAILABLE, admin00Id, new Date(123, 4, 12), 500.00, Item.ItemCondition.NEW, "laptop.jpg");
            Item item17 = new Item(null, "White-Out Correction Fluid", "High-quality white-out correction fluid for error-free corrections", "Stationery", Item.ItemStatus.AVAILABLE, owner2Id, new Date(123, 5, 20), 1.50, Item.ItemCondition.NEW, "typex.jpg");
            Item item18 = new Item(null, "Noise-Cancelling Headphones", "Over-ear headphones with noise-cancelling technology for immersive sound", "Electronics", Item.ItemStatus.AVAILABLE, owner3Id, new Date(124, 7, 11), 40.00, Item.ItemCondition.NEW, "headphones.jpg");
            Item item19 = new Item(null, "Precision Eraser", "Precision eraser for fine pencil erasures and clean work", "Stationery", Item.ItemStatus.AVAILABLE, owner4Id, new Date(1234, 12, 28), 0.50, Item.ItemCondition.NEW, "eraser.jpg");
            Item item20 = new Item(null, "Algorithms Textbook", "In-depth textbook covering algorithms for computer science students", "Books", Item.ItemStatus.AVAILABLE, owner5Id, new Date(123, 1, 7), 35.00, Item.ItemCondition.USED, "algorithms_book.jpg");
            Item item21 = new Item(null, "Arduino Development Kit", "Kit with Arduino board, sensors, and cables for electronics projects", "Electronics", Item.ItemStatus.BORROWED, owner6Id, new Date(123, 9, 15), 45.00, Item.ItemCondition.NEW, "arduino_kit.jpg");
            Item item22 = new Item(null, "Full HD Webcam", "High-definition webcam ideal for video conferences and recordings", "Electronics", Item.ItemStatus.BORROWED, owner7Id, new Date(123, 8, 4), 35.00, Item.ItemCondition.LIKE_NEW, "webcam.jpg");
            Item item23 = new Item(null, "1TB External Hard Drive", "Portable external hard drive for backups and data storage", "Electronics", Item.ItemStatus.AVAILABLE, owner8Id, new Date(124, 6, 19), 60.00, Item.ItemCondition.USED, "external_hdd.jpg");
            Item item24 = new Item(null, "Digital Multimeter", "Digital multimeter for measuring voltage, current, and resistance", "Tools", Item.ItemStatus.AVAILABLE, owner9Id, new Date(124, 1, 5), 25.00, Item.ItemCondition.NEW, "multimeter.jpg");
            Item item25 = new Item(null, "Portable Printer", "Wireless portable printer for printing documents anywhere", "Electronics", Item.ItemStatus.BORROWED, owner10Id, new Date(124, 10, 22), 80.00, Item.ItemCondition.GOOD, "portable_printer.jpg");
            Item item26 = new Item(null, "Portable WiFi Router", "Portable WiFi router for creating wireless networks in areas without connection", "Electronics", Item.ItemStatus.AVAILABLE, owner11Id, new Date(124, 4, 14), 55.00, Item.ItemCondition.GOOD, "wifi_router.jpg");
            Item item27 = new Item(null, "Rechargeable LED Desk Lamp", "LED desk lamp with rechargeable battery for studying at night", "Accessories", Item.ItemStatus.AVAILABLE, owner12Id, new Date(124, 6, 30), 20.00, Item.ItemCondition.USED, "led_lamp.jpg");
            Item item28 = new Item(null, "Universal Laptop Charger", "Universal charger compatible with multiple laptop models", "Electronics", Item.ItemStatus.AVAILABLE, owner13Id, new Date(124, 8, 8), 30.00, Item.ItemCondition.LIKE_NEW, "laptop_charger.jpg");
            Item item29 = new Item(null, "Raspberry Pi Board", "Raspberry Pi development board for programming and electronics projects", "Electronics", Item.ItemStatus.AVAILABLE, owner14Id, new Date(124, 3, 16), 35.00, Item.ItemCondition.USED, "raspberry_pi.jpg");
            Item item30 = new Item(null, "USB Digital Microscope", "Portable USB digital microscope for detailed analysis and studies", "Electronics", Item.ItemStatus.AVAILABLE, owner15Id, new Date(124, 11, 27), 70.00, Item.ItemCondition.GOOD, "digital_microscope.jpg");
            Item item31 = new Item(null, "Calculus Textbook", "A comprehensive textbook covering calculus concepts for university students", "Books", Item.ItemStatus.UNAVAILABLE, owner1Id, new Date(124, 0, 12), 55.00, Item.ItemCondition.USED, "calculus_textbook.jpg");
            Item item32 = new Item(null, "Campus Umbrella", "Durable umbrella to shield students from rain on campus", "Accessories", Item.ItemStatus.BORROWED, owner2Id, new Date(124, 1, 20), 20.00, Item.ItemCondition.GOOD, "campus_umbrella.jpg");
            Item item33 = new Item(null, "Wireless Earbuds", "Compact and high-quality wireless earbuds for everyday use", "Electronics", Item.ItemStatus.BORROWED, owner3Id, new Date(124, 7, 5), 35.00, Item.ItemCondition.GOOD, "wireless_earbuds.jpg");
            Item item34 = new Item(null, "Laptop Cooling Pad", "Cooling pad to enhance laptop performance during heavy use", "Electronics", Item.ItemStatus.AVAILABLE, owner4Id, new Date(124, 3, 18), 25.00, Item.ItemCondition.VERY_USED, "laptop_cooling_pad.jpg");
            Item item35 = new Item(null, "Multiport USB Hub", "USB hub with multiple ports to connect several devices simultaneously", "Electronics", Item.ItemStatus.AVAILABLE, owner5Id, new Date(124, 11, 2), 18.00, Item.ItemCondition.NEW, "multiport_usb_hub.jpg");
            Item item36 = new Item(null, "Stainless Steel Thermos", "Insulated thermos to keep beverages hot or cold for long hours", "Accessories", Item.ItemStatus.AVAILABLE, owner6Id, new Date(124, 10, 25), 30.00, Item.ItemCondition.VERY_USED, "stainless_steel_thermos.jpg");
            Item item37 = new Item(null, "Bluetooth Presenter Remote", "Remote control for presentations with Bluetooth connectivity", "Electronics", Item.ItemStatus.AVAILABLE, owner7Id, new Date(124, 8, 14), 22.00, Item.ItemCondition.USED, "bluetooth_presenter_remote.jpg");
            Item item38 = new Item(null, "Desk Lamp", "LED desk lamp with adjustable brightness for late-night studies", "Furniture", Item.ItemStatus.AVAILABLE, owner8Id, new Date(124, 5, 30), 40.00, Item.ItemCondition.NEW, "desk_lamp.jpg");
            Item item39 = new Item(null, "USB 16GB", "portable USB for quick data transfer storage or presentations", "Electronics", Item.ItemStatus.AVAILABLE, owner9Id, new Date(124, 4, 7), 90.00, Item.ItemCondition.USED, "portable_ssd_drive.jpg");
            Item item40 = new Item(null, "Advanced English Dictionary", "Comprehensive dictionary for academic writing and research", "Books", Item.ItemStatus.AVAILABLE, admin00Id, new Date(124, 2, 3), 40.00, Item.ItemCondition.NEW, "advanced_english_dictionary.jpg");
            Item item41 = new Item(null, "Basic Pencil Case", "Durable pencil case to store pens and pencils", "Stationery", Item.ItemStatus.AVAILABLE, owner1Id, new Date(125, 2, 14), 8.50, Item.ItemCondition.NEW, "pencil_case.jpg");
            Item item42 = new Item(null, "Ballpoint Pens Set", "Set of high-quality ballpoint pens for everyday writing", "Stationery", Item.ItemStatus.AVAILABLE, owner2Id, new Date(125, 0, 10), 5.00, Item.ItemCondition.VERY_USED, "ballpoint_pens.jpg");
            Item item43 = new Item(null, "Scientific Calculator", "Reliable scientific calculator for complex calculations", "Calculators", Item.ItemStatus.AVAILABLE, owner3Id, new Date(125, 1, 25), 25.00, Item.ItemCondition.USED, "scientific_calculator.jpg");
            Item item44 = new Item(null, "Artificial Intelligence: A Modern Approach", "Comprehensive textbook covering modern AI concepts", "Books", Item.ItemStatus.AVAILABLE, owner4Id, new Date(125, 3, 5), 60.00, Item.ItemCondition.NEW, "ai_modern_approach.jpg");
            Item item45 = new Item(null, "Introduction to Computer Science", "Fundamental textbook on computer science principles", "Books", Item.ItemStatus.AVAILABLE, owner5Id, new Date(125, 2, 15), 55.00, Item.ItemCondition.NEW, "intro_computer_science.jpg");
            Item item46 = new Item(null, "Introduction to Business Administration (ADE)", "Fundamentals of Business Administration for university students", "Books", Item.ItemStatus.AVAILABLE, owner1Id, new Date(125, 0, 15), 40.00, Item.ItemCondition.USED, "ade_book.jpg");
            Item item47 = new Item(null, "Natural Language Processing with Python", "Comprehensive guide to NLP techniques using Python for text analysis", "Books", Item.ItemStatus.AVAILABLE, owner2Id, new Date(125,2, 2), 45.00, Item.ItemCondition.NEW, "nlp_book.jpg");
            Item item48 = new Item(null, "Reinforcement Learning: An Introduction", "A detailed introduction to reinforcement learning algorithms and applications", "Books", Item.ItemStatus.AVAILABLE, owner3Id, new Date(125, 1, 14), 50.00, Item.ItemCondition.NEW, "reinforcement_learning.jpg");
            Item item49 = new Item(null, "Ethical Hacking: Practical Guide", "A practical guide to ethical hacking and penetration testing", "Books", Item.ItemStatus.AVAILABLE, owner4Id, new Date(125, 2, 9), 38.00, Item.ItemCondition.NEW, "ethical_hacking_book.jpg");
            Item item50 = new Item(null, "Information Systems: Theory and Practice", "Comprehensive textbook covering information systems concepts and applications", "Books", Item.ItemStatus.AVAILABLE, owner5Id, new Date(125, 3, 1), 42.00, Item.ItemCondition.NEW, "information_systems_book.jpg");

            // Save items to the database
            itemRepository.save(item1);  itemRepository.save(item2);  itemRepository.save(item3);  itemRepository.save(item4);  itemRepository.save(item5);
            itemRepository.save(item6);  itemRepository.save(item7);  itemRepository.save(item8);  itemRepository.save(item9);  itemRepository.save(item10);
            itemRepository.save(item11); itemRepository.save(item12); itemRepository.save(item13); itemRepository.save(item14); itemRepository.save(item15);
            itemRepository.save(item16); itemRepository.save(item17); itemRepository.save(item18); itemRepository.save(item19); itemRepository.save(item20);
            itemRepository.save(item21); itemRepository.save(item22); itemRepository.save(item23); itemRepository.save(item24); itemRepository.save(item25);
            itemRepository.save(item26); itemRepository.save(item27); itemRepository.save(item28); itemRepository.save(item29); itemRepository.save(item30);
            itemRepository.save(item31); itemRepository.save(item32); itemRepository.save(item33); itemRepository.save(item34); itemRepository.save(item35);
            itemRepository.save(item36); itemRepository.save(item37); itemRepository.save(item38); itemRepository.save(item39); itemRepository.save(item40);
            itemRepository.save(item41); itemRepository.save(item42); itemRepository.save(item43); itemRepository.save(item44); itemRepository.save(item45);
            itemRepository.save(item46); itemRepository.save(item47); itemRepository.save(item48); itemRepository.save(item49); itemRepository.save(item50);

            System.out.println("Items inserted successfully.");
        }
    }
}
