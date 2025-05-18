package com.student_loan.unit.controller;

import com.student_loan.controller.ItemController;
import com.student_loan.dtos.ItemRecord;
import com.student_loan.model.Item;
import com.student_loan.model.User;
import com.student_loan.service.ItemService;
import com.student_loan.service.LoanService;
import com.student_loan.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UnitItemControllerTest {

    private ItemController itemController;
    @Mock private ItemService itemService;
    @Mock private UserService userService;
    @Mock private LoanService loanService;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        itemController = new ItemController(itemService, userService, loanService);
        // Default security context
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user@example.com");
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /items - Unauthorized when no user principal")
    void getAllItems_Unauthorized() {
        when(userService.getUserByEmail("user@example.com")).thenReturn(null);
        ResponseEntity<List<Item>> resp = itemController.getAllItems();
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertTrue(resp.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /items - Success when authenticated")
    void getAllItems_Success() {
        User u = new User(); u.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        when(itemService.getAllItems()).thenReturn(Arrays.asList(new Item()));
        ResponseEntity<List<Item>> resp = itemController.getAllItems();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertFalse(resp.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /items/available - Unauthorized when no principal")
    void getAvailableItems_Unauthorized() {
        when(userService.getUserByEmail("user@example.com")).thenReturn(null);
        ResponseEntity<List<Item>> resp = itemController.getAvailableItems();
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertTrue(resp.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /items/available - Success returns available items")
    void getAvailableItems_Success() {
        User u = new User(); u.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        when(itemService.getAvailableItems()).thenReturn(Arrays.asList(new Item()));
        ResponseEntity<List<Item>> resp = itemController.getAvailableItems();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("GET /items/user/{id} - Unauthorized when no principal")
    void getItemsByUser_Unauthorized() {
        when(userService.getUserByEmail("user@example.com")).thenReturn(null);
        ResponseEntity<List<Item>> resp = itemController.getItemsByUser(5L);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /items/user/{id} - Success when items exist")
    void getItemsByUser_Success() {
        User u = new User(); u.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        when(itemService.getItemsByUser(5L)).thenReturn(Arrays.asList(new Item()));
        ResponseEntity<List<Item>> resp = itemController.getItemsByUser(5L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("GET /items/user/{id} - Bad request on service exception")
    void getItemsByUser_BadRequest() {
        User u = new User(); u.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        when(itemService.getItemsByUser(5L)).thenThrow(new RuntimeException());
        ResponseEntity<List<Item>> resp = itemController.getItemsByUser(5L);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /items/{id} - Unauthorized when no principal or missing item")
    void getItemById_UnauthorizedOrMissing() {
        when(userService.getUserByEmail("user@example.com")).thenReturn(null);
        ResponseEntity<Item> resp1 = itemController.getItemById(10L);
        assertEquals(HttpStatus.UNAUTHORIZED, resp1.getStatusCode());
        User u = new User(); u.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        when(itemService.getItemById(10L)).thenReturn(Optional.empty());
        ResponseEntity<Item> resp2 = itemController.getItemById(10L);
        assertEquals(HttpStatus.UNAUTHORIZED, resp2.getStatusCode());
    }

    @Test
    @DisplayName("GET /items/{id} - Forbidden when not owner")
    void getItemById_Forbidden() {
        User u = new User(); u.setEmail("user@example.com"); u.setId(1L);
        Item item = new Item(); item.setOwner(2L);
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        when(itemService.getItemById(10L)).thenReturn(Optional.of(item));
        ResponseEntity<Item> resp = itemController.getItemById(10L);
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /items/{id} - Success when owner")
    void getItemById_Success() {
        User u = new User(); u.setEmail("user@example.com"); u.setId(3L);
        Item item = new Item(); item.setOwner(3L);
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        when(itemService.getItemById(20L)).thenReturn(Optional.of(item));
        ResponseEntity<Item> resp = itemController.getItemById(20L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(item, resp.getBody());
    }

    @Test
    @DisplayName("GET /items/lent - Unauthorized when no user")
    void getLentItems_Unauthorized() {
        when(userService.getUserByEmail("user@example.com")).thenReturn(null);
        ResponseEntity<List<Item>> resp = itemController.getLentItemsByUser();
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /items/lent - Success returns lent items")
    void getLentItems_Success() {
        User u = new User(); u.setEmail("user@example.com"); u.setId(4L);
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        when(loanService.getLentItemsIdByUser(4L)).thenReturn(Arrays.asList(100L));
        when(itemService.getItemsById(Arrays.asList(100L))).thenReturn(Arrays.asList(new Item()));
        ResponseEntity<List<Item>> resp = itemController.getLentItemsByUser();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /items/lent - Internal server error on exception")
    void getLentItems_Error() {
        User u = new User(); u.setEmail("user@example.com"); u.setId(4L);
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        when(loanService.getLentItemsIdByUser(4L)).thenThrow(new RuntimeException());
        ResponseEntity<List<Item>> resp = itemController.getLentItemsByUser();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /items/borrowed - Unauthorized when no user")
    void getBorrowedItems_Unauthorized() {
        when(userService.getUserByEmail("user@example.com")).thenReturn(null);
        ResponseEntity<List<Item>> resp = itemController.getBorrowedItemsByUser();
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /items/borrowed - Success returns borrowed items")
    void getBorrowedItems_Success() {
        User u = new User(); u.setEmail("user@example.com"); u.setId(5L);
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        when(loanService.getBorrowedItemsIdByUser(5L)).thenReturn(Arrays.asList(200L));
        when(itemService.getItemsById(Arrays.asList(200L))).thenReturn(Arrays.asList(new Item()));
        ResponseEntity<List<Item>> resp = itemController.getBorrowedItemsByUser();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("GET /items/borrowed - Internal server error on exception")
    void getBorrowedItems_Error() {
        User u = new User(); u.setEmail("user@example.com"); u.setId(5L);
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        when(loanService.getBorrowedItemsIdByUser(5L)).thenThrow(new RuntimeException());
        ResponseEntity<List<Item>> resp = itemController.getBorrowedItemsByUser();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    @Test
    @DisplayName("POST /items/create - Unauthorized when no principal")
    void createItem_Unauthorized() {
        when(userService.getUserByEmail("user@example.com")).thenReturn(null);

        ResponseEntity<String> resp = itemController.createItem(
            new ItemRecord("n","d","c","6", "SG9sYU11bmRvMTIz","AVAILABLE","NEW")
        );
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("POST /items/create - Success on create")
    void createItem_Success() {
        User u = new User(); u.setEmail("user@example.com"); u.setId(6L);
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        Item saved = new Item();
        when(itemService.saveItem(any())).thenReturn(saved);

        ResponseEntity<String> resp = itemController.createItem(
            new ItemRecord("n","d","c","6", "SG9sYU11bmRvMTIz","AVAILABLE","NEW")
        );
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    @Test
    @DisplayName("POST /items/create - Bad request on save exception")
    void createItem_BadRequestOnError() {
        User u = new User(); u.setEmail("user@example.com"); u.setId(7L);
        when(userService.getUserByEmail("user@example.com")).thenReturn(u);
        when(itemService.saveItem(any())).thenThrow(new RuntimeException("oops"));

        ResponseEntity<String> resp = itemController.createItem(
            new ItemRecord("n","d","c","6", "SG9sYU11bmRvMTIz","AVAILABLE","NEW")
        );
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("oops", resp.getBody());
    }

    @Test
    @DisplayName("POST /items?token - Unauthorized when invalid token")
    void createItemWithToken_Unauthorized() {
        when(userService.getUserByToken("bad")).thenReturn(null);

        ResponseEntity<String> resp = itemController.createItem(
            new ItemRecord("n","d","c","6", "SG9sYU11bmRvMTIz","AVAILABLE","NEW"), "bad"
        );
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("POST /items?token - Success on create with token")
    void createItemWithToken_Success() {
        User u = new User(); u.setId(8L);
        when(userService.getUserByToken("tok")).thenReturn(u);
        Item saved = new Item();
        when(itemService.saveItem(any())).thenReturn(saved);

        ResponseEntity<String> resp = itemController.createItem(
            new ItemRecord("n","d","c","6", "SG9sYU11bmRvMTIz","AVAILABLE","NEW"), "tok"
        );
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    @Test
    @DisplayName("POST /items?token - Bad request on save exception with token")
    void createItemWithToken_BadRequestOnError() {
        User u = new User(); u.setId(9L);
        when(userService.getUserByToken("tok")).thenReturn(u);
        when(itemService.saveItem(any())).thenThrow(new RuntimeException("fail"));

        ResponseEntity<String> resp = itemController.createItem(
            new ItemRecord("n","d","c","6", "SG9sYU11bmRvMTIz","AVAILABLE","NEW"), "tok"
        );
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("fail", resp.getBody());
    }

    @Test
    @DisplayName("PUT /items/{id} - Unauthorized when token invalid")
    void updateItem_TokenInvalid() {
        when(userService.getUserByToken("bad")).thenReturn(null);

        ResponseEntity<String> resp = itemController.updateItem(
            1L,
            new ItemRecord("n","d","c","6", "SG9sYU11bmRvMTIz","AVAILABLE","NEW"),
            "bad"
        );
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /items/{id} - Unauthorized when not owner nor admin")
    void updateItem_NotOwnerNorAdmin() {
        User u = new User(); u.setId(10L); u.setAdmin(false);
        when(userService.getUserByToken("tok")).thenReturn(u);

        Item it = new Item(); it.setOwner(11L);
        when(itemService.getItemById(5L)).thenReturn(Optional.of(it));

        ResponseEntity<String> resp = itemController.updateItem(
            5L,
            new ItemRecord("n","d","c","6", "SG9sYU11bmRvMTIz","AVAILABLE","NEW"),
            "tok"
        );
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /items/{id} - Bad request when save throws")
    void updateItem_SaveThrows() {
        User u = new User(); u.setId(13L); u.setAdmin(true);
        when(userService.getUserByToken("tok")).thenReturn(u);

        Item it = new Item(); it.setOwner(13L);
        when(itemService.getItemById(7L)).thenReturn(Optional.of(it));
        when(itemService.saveItem(any())).thenThrow(new RuntimeException("x"));

        ResponseEntity<String> resp = itemController.updateItem(
            7L,
            new ItemRecord("n","d","c","6", "SG9sYU11bmRvMTIz","AVAILABLE","NEW"),
            "tok"
        );
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals("x", resp.getBody());
    }

    @Test
    @DisplayName("PUT /items/{id} - Success when owner")
    void updateItem_OwnerSuccess() {
        User u = new User(); u.setId(14L); u.setAdmin(false);
        when(userService.getUserByToken("tok")).thenReturn(u);

        Item it = new Item(); it.setOwner(14L);
        when(itemService.getItemById(8L)).thenReturn(Optional.of(it));
        when(itemService.saveItem(any())).thenReturn(it);

        ResponseEntity<String> resp = itemController.updateItem(
            8L,
            new ItemRecord("n","d","c","6", "SG9sYU11bmRvMTIz","AVAILABLE","NEW"),
            "tok"
        );
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /items/{id} - Bad request on invalid status")
    void update_InvalidStatus_ThrowsException() {
        User u = new User(); u.setId(2L); u.setAdmin(true);
        when(userService.getUserByToken("t")).thenReturn(u);
        Item existing = new Item(); existing.setOwner(2L);
        when(itemService.getItemById(3L)).thenReturn(Optional.of(existing));
        ItemRecord rec = new ItemRecord(null, null, null, null, null,"XYZ", null);

        ResponseEntity<String> resp = itemController.updateItem(3L, rec, "t");
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertTrue(resp.getBody().contains("No enum constant"));
    }

    @Test
    @DisplayName("PUT /items/{id} - Success when admin")
    void updateItem_AdminSuccess() {
        User u = new User(); u.setId(15L); u.setAdmin(true);
        when(userService.getUserByToken("tok")).thenReturn(u);

        Item it = new Item(); it.setOwner(99L);
        when(itemService.getItemById(9L)).thenReturn(Optional.of(it));
        when(itemService.saveItem(any())).thenReturn(it);

        ResponseEntity<String> resp = itemController.updateItem(
            9L,
            new ItemRecord("new","new","new","6", "new", "AVAILABLE","NEW"),
            "tok"
        );
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("PUT /items/{id} - Success on partial update (only name)")
    void update_PartialFields_OK() {
        User u = new User(); u.setId(2L); u.setAdmin(false);
        when(userService.getUserByToken("tok")).thenReturn(u);
        Item existing = new Item();
        existing.setOwner(2L);
        existing.setName("old");
        existing.setDescription("desc");
        existing.setCategory("cat");
        existing.setImage("img");
        existing.setStatus(Item.ItemStatus.AVAILABLE);
        existing.setCondition(Item.ItemCondition.NEW);
        when(itemService.getItemById(5L)).thenReturn(Optional.of(existing));

        ItemRecord rec = new ItemRecord("newName", null, null, null, null, null, null);
        when(itemService.saveItem(any())).thenReturn(existing);

        ResponseEntity<String> resp = itemController.updateItem(5L, rec, "tok");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("newName", existing.getName());

        assertEquals("desc", existing.getDescription());
        assertEquals(Item.ItemStatus.AVAILABLE, existing.getStatus());
    }

    @Test
    @DisplayName("PUT /items/{id} - Exception when missing item in auth check")
    void updateItem_ItemNotFound() {
        User u = new User(); u.setId(12L); u.setAdmin(true);
        when(userService.getUserByToken("tok")).thenReturn(u);
        when(itemService.getItemById(6L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
            itemController.updateItem(6L,
                new ItemRecord("n","d","c","6", "SG9sYU11bmRvMTIz","AVAILABLE","NEW"),
                "tok"
            )
        );
    }

    @Test
    @DisplayName("DELETE /items/{id} - Unauthorized if no user")
    void deleteItem_NoUser_Unauthorized() {
        when(userService.getUserByToken("tok")).thenReturn(null);
        ResponseEntity<String> resp = itemController.deleteItem(1L, "tok");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /items/{id} - Unauthorized when not owner nor admin")
    void deleteItem_NotOwnerNorAdmin() {
        User u = new User(); u.setId(8L); u.setAdmin(false);
        when(userService.getUserByToken("tok")).thenReturn(u);
        Item it = new Item(); it.setOwner(9L);
        when(itemService.getItemById(1L)).thenReturn(Optional.of(it));

        ResponseEntity<String> resp = itemController.deleteItem(1L, "tok");
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

        @Test
    @DisplayName("DELETE /items/{id} - Returns OK when admin and item missing")
    void deleteItem_ItemMissing_AdminOk() {
        User admin = new User();
        admin.setId(10L);
        admin.setAdmin(true);
        when(userService.getUserByToken("tok")).thenReturn(admin);
        when(itemService.getItemById(2L)).thenReturn(Optional.empty());

        doNothing().when(itemService).deleteItem(2L);

        ResponseEntity<String> resp = itemController.deleteItem(2L, "tok");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /items/{id} - Exception when non-admin hits missing item in auth check")
    void deleteItem_ItemMissing_NonAdminThrows() {
        User user = new User();
        user.setId(20L);
        user.setAdmin(false);
        when(userService.getUserByToken("tok")).thenReturn(user);
        when(itemService.getItemById(3L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
            itemController.deleteItem(3L, "tok")
        );
    }

    @Test
    @DisplayName("DELETE /items/{id} - Success when owner")
    void deleteItem_SuccessOwner() {
        User u = new User(); u.setId(9L); u.setAdmin(false);
        when(userService.getUserByToken("tok")).thenReturn(u);
        Item it = new Item(); it.setOwner(9L);
        when(itemService.getItemById(3L)).thenReturn(Optional.of(it));
        doNothing().when(itemService).deleteItem(3L);

        ResponseEntity<String> resp = itemController.deleteItem(3L, "tok");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /items/{id} - Success when admin")
    void deleteItem_SuccessAdmin() {
        User u = new User(); u.setId(11L); u.setAdmin(true);
        when(userService.getUserByToken("tok")).thenReturn(u);
        Item it = new Item(); it.setOwner(99L);
        when(itemService.getItemById(4L)).thenReturn(Optional.of(it));
        doNothing().when(itemService).deleteItem(4L);

        ResponseEntity<String> resp = itemController.deleteItem(4L, "tok");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /items/{id} - RuntimeException propagates")
    void deleteItem_Exception() {
        User u = new User(); u.setId(12L); u.setAdmin(true);
        when(userService.getUserByToken("tok")).thenReturn(u);
        Item it = new Item(); it.setOwner(100L);
        when(itemService.getItemById(5L)).thenReturn(Optional.of(it));
        doThrow(new RuntimeException("delErr")).when(itemService).deleteItem(5L);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            itemController.deleteItem(5L, "tok")
        );
        assertEquals("delErr", ex.getMessage());
    }
}