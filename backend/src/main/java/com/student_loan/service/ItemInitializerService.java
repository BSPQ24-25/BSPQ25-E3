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

    @PostConstruct
    @Transactional
    public void insertDefaultItems() {

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

        // Set of items to insert
        Item item1 = new Item(null, "Artificial Intelligence Book", "Book on Artificial Intelligence for university students", "Books", Item.ItemStatus.AVAILABLE, owner1Id, new Date(), 29.99, Item.ItemCondition.NEW, "ai_book.jpg");
        Item item2 = new Item(null,"HDMI 2.0 Cable", "Premium quality HDMI 2.0 cable for video and audio transmission", "Electronics", Item.ItemStatus.AVAILABLE, owner2Id, new Date(), 15.00, Item.ItemCondition.LIKE_NEW, "hdmi_2_0_cable.jpg");
        Item item3 = new Item(null,"Academic Planner", "A planner for organizing academic work and deadlines", "Stationery", Item.ItemStatus.AVAILABLE, owner3Id, new Date(), 12.99, Item.ItemCondition.NEW, "academic_planner.jpg");
        Item item4 = new Item(null,"Graphing Scientific Calculator", "Advanced scientific calculator with graphing capabilities", "Calculators", Item.ItemStatus.AVAILABLE, owner4Id, new Date(), 50.00, Item.ItemCondition.LIKE_NEW, "graphing_scientific_calculator.jpg");
        Item item5 = new Item(null,"Ergonomic Wireless Keyboard", "Ergonomically designed wireless keyboard for comfortable typing", "Electronics", Item.ItemStatus.AVAILABLE, owner5Id, new Date(), 28.00, Item.ItemCondition.GOOD, "ergonomic_wireless_keyboard.jpg");
        Item item6 = new Item(null,"Portable Bluetooth Speaker", "Compact Bluetooth speaker for clear sound anywhere", "Electronics", Item.ItemStatus.AVAILABLE, owner6Id, new Date(), 40.00, Item.ItemCondition.NEW, "portable_bluetooth_speaker.jpg");
        Item item7 = new Item(null,"Adjustable Smartphone Stand", "Multi-functional adjustable stand for smartphones and tablets", "Accessories", Item.ItemStatus.AVAILABLE, owner7Id, new Date(), 12.00, Item.ItemCondition.NEW, "adjustable_smartphone_stand.jpg");
        Item item8 = new Item(null,"32GB USB Flash Drive", "USB flash drive with 32GB capacity for fast data transfer", "Electronics", Item.ItemStatus.AVAILABLE, owner8Id, new Date(), 10.00, Item.ItemCondition.LIKE_NEW, "32gb_usb_flash_drive.jpg");
        Item item9 = new Item(null,"Compact Tool Kit", "Multi-purpose tool kit for small repairs and DIY projects", "Tools", Item.ItemStatus.AVAILABLE, owner9Id, new Date(), 20.00, Item.ItemCondition.GOOD, "compact_tool_kit.jpg");
        Item item10 = new Item(null,"Digital Drawing Tablet", "Graphics tablet with high precision for digital art and design", "Electronics", Item.ItemStatus.AVAILABLE, owner10Id, new Date(), 150.00, Item.ItemCondition.NEW, "digital_drawing_tablet.jpg");
        Item item11 = new Item(null,"Introduction to Deep Learning", "Comprehensive textbook on Deep Learning concepts and applications for beginners and advanced students", "Books", Item.ItemStatus.AVAILABLE, owner1Id, new Date(), 25.99, Item.ItemCondition.NEW, "ai_book.jpg");
        Item item12 = new Item(null,"Fast USB-C Charging Cable", "High-speed USB-C charger for fast charging of mobile devices", "Chargers", Item.ItemStatus.AVAILABLE, owner2Id, new Date(), 10.50, Item.ItemCondition.LIKE_NEW, "charger_type_c.jpg");
        Item item13 = new Item(null,"Advanced Scientific Calculator", "Scientific calculator with advanced functions for university-level students", "Calculators", Item.ItemStatus.AVAILABLE, owner3Id, new Date(), 15.00, Item.ItemCondition.GOOD, "calculator.jpg");
        Item item14 = new Item(null,"Academic Student Notebook", "100-page notebook for note-taking and academic purposes", "Stationery", Item.ItemStatus.AVAILABLE, owner4Id, new Date(), 2.50, Item.ItemCondition.NEW, "notebook.jpg");
        Item item15 = new Item(null,"Ergonomic Wireless Mouse", "Wireless ergonomic mouse designed for extended computer use", "Electronics", Item.ItemStatus.AVAILABLE, owner5Id, new Date(), 12.00, Item.ItemCondition.LIKE_NEW, "wireless_mouse.jpg");
        Item item16 = new Item(null,"Portable Academic Laptop", "Laptop with powerful specs for academic purposes, programming, and research", "Electronics", Item.ItemStatus.AVAILABLE, owner6Id, new Date(), 500.00, Item.ItemCondition.NEW, "laptop.jpg");
        Item item17 = new Item(null,"White-Out Correction Fluid", "High-quality white-out correction fluid for error-free corrections", "Stationery", Item.ItemStatus.AVAILABLE, owner7Id, new Date(), 1.50, Item.ItemCondition.NEW, "typex.jpg");
        Item item18 = new Item(null,"Noise-Cancelling Headphones", "Over-ear headphones with noise-cancelling technology for immersive sound", "Electronics", Item.ItemStatus.AVAILABLE, owner8Id, new Date(), 40.00, Item.ItemCondition.NEW, "headphones.jpg");
        Item item19 = new Item(null,"Precision Eraser", "Precision eraser for fine pencil erasures and clean work", "Stationery", Item.ItemStatus.AVAILABLE, owner9Id, new Date(), 0.50, Item.ItemCondition.NEW, "eraser.jpg");
        Item item20 = new Item(null,"Algorithms Textbook", "In-depth textbook covering algorithms for computer science students", "Books", Item.ItemStatus.AVAILABLE, owner10Id, new Date(), 35.00, Item.ItemCondition.LIKE_NEW, "algorithms_book.jpg");


        // Save items to the database
        itemRepository.save(item1); itemRepository.save(item2); itemRepository.save(item3); itemRepository.save(item4); itemRepository.save(item5);
        itemRepository.save(item6); itemRepository.save(item7); itemRepository.save(item8); itemRepository.save(item9); itemRepository.save(item10);
        itemRepository.save(item11); itemRepository.save(item12); itemRepository.save(item13); itemRepository.save(item14); itemRepository.save(item15);
        itemRepository.save(item16); itemRepository.save(item17); itemRepository.save(item18); itemRepository.save(item19); itemRepository.save(item10);

        System.out.println("Another set of items inserted successfully.");
    }
}
