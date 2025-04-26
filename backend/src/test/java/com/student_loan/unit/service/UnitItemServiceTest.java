package com.student_loan.unit.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.student_loan.model.Item;
import com.student_loan.model.Item.ItemStatus;
import com.student_loan.model.Item.ItemCondition;
import com.student_loan.repository.ItemRepository;
import com.student_loan.repository.UserRepository;

import com.student_loan.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;


class UnitItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllItems() {
        Item item1 = new Item(1L, "Laptop", "MacBook", "Electronics", ItemStatus.AVAILABLE, 1L, new Date(), 1000.0, ItemCondition.NEW, "image1.jpg");
        Item item2 = new Item(2L, "Monitor", "Dell", "Electronics", ItemStatus.AVAILABLE, 1L, new Date(), 300.0, ItemCondition.GOOD, "image2.jpg");
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        List<Item> result = itemService.getAllItems();

        assertEquals(2, result.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testGetItemById_ItemFound() {
        Item item = new Item(1L, "Laptop", "MacBook", "Electronics", ItemStatus.AVAILABLE, 1L, new Date(), 1000.0, ItemCondition.NEW, "image1.jpg");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Optional<Item> result = itemService.getItemById(1L);

        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
    }

    @Test
    void testGetItemById_ItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Item> result = itemService.getItemById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetItemsByUser_UserExists() {
        User user = new User();
        user.setId(1L);
        Item item = new Item(1L, "Laptop", "MacBook", "Electronics", ItemStatus.AVAILABLE, 1L, new Date(), 1000.0, ItemCondition.NEW, "image1.jpg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwner(1L)).thenReturn(List.of(item));

        List<Item> items = itemService.getItemsByUser(1L);

        assertEquals(1, items.size());
        assertEquals("Laptop", items.get(0).getName());
    }

    @Test
    void testGetItemsByUser_UserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            itemService.getItemsByUser(99L);
        });

        assertTrue(ex.getMessage().contains("User not found with id: 99"));
    }

    @Test
    void testSaveItem_Success() {
        User user = new User();
        user.setId(2L);
        Item item = new Item(1L, "Phone", "Samsung", "Electronics", ItemStatus.AVAILABLE, 2L, new Date(), 500.0, ItemCondition.NEW, "image1.jpg");

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);

        Item saved = itemService.saveItem(item);

        assertNotNull(saved);
        assertEquals("Phone", saved.getName());
    }

    @Test
    void testSaveItem_UserNotFound() {
        Item item = new Item(1L, "Tablet", "iPad", "Electronics", ItemStatus.AVAILABLE, 5L, new Date(), 700.0, ItemCondition.GOOD, "image1.jpg");
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            itemService.saveItem(item);
        });

        assertTrue(ex.getMessage().contains("Owner not found with id: 5"));
    }

    @Test
    void testCreateItem_Success() {
        User user = new User();
        user.setId(4L);
        Item item = new Item(3L, "Desk", "Wooden", "Furniture", ItemStatus.AVAILABLE, 4L, new Date(), 150.0, ItemCondition.GOOD, "image3.jpg");

        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(3L)).thenReturn(Optional.empty());
        when(itemRepository.save(item)).thenReturn(item);

        Item created = itemService.createItem(item);

        assertNotNull(created);
        assertEquals("Desk", created.getName());
    }

    @Test
    void testCreateItem_ItemAlreadyExists() {
        Item item = new Item(3L, "Desk", "Wooden", "Furniture", ItemStatus.AVAILABLE, 4L, new Date(), 150.0, ItemCondition.GOOD, "image3.jpg");
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            itemService.createItem(item);
        });

        assertTrue(ex.getMessage().contains("Item already exists"));
    }

    @Test
    void testCreateItem_OwnerNotFound() {
        Item item = new Item(4L, "Chair", "Plastic", "Furniture", ItemStatus.AVAILABLE, 8L, new Date(), 40.0, ItemCondition.VERY_USED, "image4.jpg");
        when(itemRepository.findById(4L)).thenReturn(Optional.empty());
        when(userRepository.findById(8L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            itemService.createItem(item);
        });

        assertTrue(ex.getMessage().contains("Owner not found with id: 8"));
    }

    @Test
    void testDeleteItem() {
        itemService.deleteItem(10L);
        verify(itemRepository, times(1)).deleteById(10L);
    }
}
