package com.student_loan.unit.controller;

import com.student_loan.controller.ItemController;
import com.student_loan.dtos.ItemRecord;
import com.student_loan.model.Item;
import com.student_loan.model.User;
import com.student_loan.service.ItemService;
import com.student_loan.service.LoanService;
import com.student_loan.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnitItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private ItemController itemController;

    private User user;
    private User adminUser;
    private Item item;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setAdmin(false); 

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setEmail("admin@example.com");
        adminUser.setAdmin(true); 

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setOwner(user.getId()); 
}


    @Test
    public void testGetAllItems_Success() {
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(itemService.getAllItems()).thenReturn(Arrays.asList(item));

        ResponseEntity<List<Item>> response = itemController.getAllItems();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetAllItems_Unauthorized() {
        when(userService.getUserByEmail(anyString())).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getAllItems();

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testGetAvailableItems_Success() {
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(itemService.getAvailableItems()).thenReturn(Arrays.asList(item));

        ResponseEntity<List<Item>> response = itemController.getAvailableItems();

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetItemsByUser_Success() {
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(itemService.getItemsByUser(anyLong())).thenReturn(Arrays.asList(item));

        ResponseEntity<List<Item>> response = itemController.getItemsByUser(1L);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetItemsByUser_Unauthorized() {
        when(userService.getUserByEmail(anyString())).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByUser(1L);

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testGetItemById_Success() {
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(itemService.getItemById(anyLong())).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetItemById_Forbidden() {
        when(userService.getUserByEmail(anyString())).thenReturn(adminUser);
        when(itemService.getItemById(anyLong())).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    public void testGetLentItemsByUser_Success() {
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(loanService.getLentItemsIdByUser(anyLong())).thenReturn(Arrays.asList(1L));
        when(itemService.getItemsById(any())).thenReturn(Arrays.asList(item));

        ResponseEntity<List<Item>> response = itemController.getLentItemsByUser();

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetBorrowedItemsByUser_Success() {
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(loanService.getBorrowedItemsIdByUser(anyLong())).thenReturn(Arrays.asList(1L));
        when(itemService.getItemsById(any())).thenReturn(Arrays.asList(item));

        ResponseEntity<List<Item>> response = itemController.getBorrowedItemsByUser();

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testCreateItem_Success() {
        when(userService.getUserByToken(anyString())).thenReturn(user);

        ItemRecord itemRecord = new ItemRecord("Laptop", "Gaming", "Electronics", "AVAILABLE", "LIKE_NEW", "image-url");

        ResponseEntity<String> response = itemController.createItem(itemRecord, "token");

        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    public void testCreateItem_Unauthorized() {
        when(userService.getUserByToken(anyString())).thenReturn(null);

        ItemRecord itemRecord = new ItemRecord("Laptop", "Gaming", "Electronics", "AVAILABLE", "LIKE_NEW", "image-url");

        ResponseEntity<String> response = itemController.createItem(itemRecord, "token");

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testDeleteItem_Success() {
        when(userService.getUserByToken(anyString())).thenReturn(adminUser);
        when(itemService.getItemById(anyLong())).thenReturn(Optional.of(item));

        ResponseEntity<String> response = itemController.deleteItem(1L, "token");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testDeleteItem_Unauthorized() {
        when(userService.getUserByToken(anyString())).thenReturn(user);
        when(itemService.getItemById(anyLong())).thenReturn(Optional.of(item));

        ResponseEntity<String> response = itemController.deleteItem(2L, "token");

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testUpdateItem_Success() {
        when(userService.getUserByToken(anyString())).thenReturn(user);
        when(itemService.getItemById(anyLong())).thenReturn(Optional.of(item));

        ItemRecord itemRecord = new ItemRecord("Laptop", "Updated Gaming", "Electronics", "AVAILABLE", "LIKE_NEW", "updated-image");

        ResponseEntity<String> response = itemController.updateItem(1L, itemRecord, "token");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testUpdateItem_Unauthorized() {
        when(userService.getUserByToken(anyString())).thenReturn(null);

        ItemRecord itemRecord = new ItemRecord("Laptop", "Updated Gaming", "Electronics", "AVAILABLE", "LIKE_NEW", "updated-image");

        ResponseEntity<String> response = itemController.updateItem(1L, itemRecord, "token");

        assertEquals(401, response.getStatusCodeValue());
    }
}
