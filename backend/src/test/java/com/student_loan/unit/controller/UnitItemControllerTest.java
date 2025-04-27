package com.student_loan.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import com.student_loan.controller.ItemController;
import com.student_loan.dtos.ItemRecord;
import com.student_loan.model.Item;
import com.student_loan.model.User;
import com.student_loan.service.ItemService;
import com.student_loan.service.LoanService;
import com.student_loan.service.UserService;

class UnitItemControllerTest {

    private ItemService itemService;
    private UserService userService;
    private LoanService loanService;
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        itemService = mock(ItemService.class);
        userService = mock(UserService.class);
        loanService = mock(LoanService.class);
        itemController = new ItemController(itemService, userService, loanService);
        
        // Configure security context
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    @DisplayName("GET /items - Unauthenticated user")
    void testGetAllItems_Unauthenticated() {
        when(userService.getUserByEmail("test@example.com")).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getAllItems();
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /items - Success")
    void testGetAllItems_Success() {
        User mockUser = new User();
        when(userService.getUserByEmail("test@example.com")).thenReturn(mockUser);
        when(itemService.getAllItems()).thenReturn(new ArrayList<>());

        ResponseEntity<List<Item>> response = itemController.getAllItems();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /items/user/{id} - Unauthorized user")
    void testGetItemsByUser_Unauthorized() {
        when(userService.getUserByEmail("test@example.com")).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByUser(1L);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /items - Invalid token")
    void testCreateItem_InvalidToken() {
        when(userService.getUserByToken("invalid")).thenReturn(null);
        
        ResponseEntity<String> response = itemController.createItem(
            new ItemRecord("Item", "Desc", "Cat", "img", "AVAILABLE", "NEW")
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /items/{id} - Unauthorized user")
    void testDeleteItem_Unauthorized() {
        User mockUser = new User();
        mockUser.setId(2L);
        mockUser.setAdmin(false); // Fix: Set admin status explicitly
        when(userService.getUserByToken("token")).thenReturn(mockUser);

        Item mockItem = new Item();
        mockItem.setOwner(1L);
        when(itemService.getItemById(1L)).thenReturn(Optional.of(mockItem));

        ResponseEntity<String> response = itemController.deleteItem(1L, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /items/{id} - Successful update")
    void testUpdateItem_Success() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setAdmin(true);
        when(userService.getUserByToken("valid-token")).thenReturn(mockUser);

        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setOwner(2L);
        existingItem.setName("Old Name");
        existingItem.setStatus(Item.ItemStatus.AVAILABLE);
        existingItem.setCondition(Item.ItemCondition.NEW);
        when(itemService.getItemById(1L)).thenReturn(Optional.of(existingItem));

        when(itemService.saveItem(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            return savedItem;
        });

        ItemRecord updateData = new ItemRecord("New Name", "Updated description", "Electronics", "image.jpg", "AVAILABLE", "USED");

        ResponseEntity<String> response = itemController.updateItem(1L, updateData, "valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected 200 OK but got " + response.getStatusCode());
        assertEquals("New Name", existingItem.getName(), "Item name was not updated");
    }

    @Test
    @DisplayName("PUT /items/{id} - Unauthorized to update")
    void testUpdateItem_Unauthorized() {
        when(userService.getUserByToken("invalid-token")).thenReturn(null);

        ResponseEntity<String> response = itemController.updateItem(
            1L, 
            new ItemRecord("Updated Name", "Updated Description", "Category", "imageUrl", "AVAILABLE", "USED"), 
            "invalid-token"
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}