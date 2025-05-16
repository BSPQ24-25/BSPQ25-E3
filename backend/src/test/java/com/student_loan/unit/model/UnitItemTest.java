package com.student_loan.unit.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.student_loan.model.Item;
import com.student_loan.model.Item.ItemCondition;
import com.student_loan.model.Item.ItemStatus;
import com.student_loan.repository.ItemRepository;

class UnitItemTest {

    private ItemRepository repository;
    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item(1L, "Laptop", "Gaming Laptop", "Electronics", ItemStatus.AVAILABLE, 1001L,
                new Date(), 1200.00, ItemCondition.NEW, "image.jpg");
        
        repository = mock(ItemRepository.class);
    }

    @Test
    @DisplayName("Should Add Item Successfully")
    void testAddItem() {
        when(repository.save(item)).thenReturn(item);

        Item result = repository.save(item);

        verify(repository, times(1)).save(item);
        assertEquals("Laptop", result.getName());
        assertEquals(ItemStatus.AVAILABLE, result.getStatus());
    }

    @Test
    @DisplayName("Should Retrieve Item By ID")
    void testGetItemById() {
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        Item result = repository.findById(1L).orElseThrow(() -> new RuntimeException("Item not found"));

        assertNotNull(result);
        assertEquals("Gaming Laptop", result.getDescription());
    }

    @Test
    @DisplayName("Should Update Item Status")
    void testUpdateItemStatus() {
        item.setStatus(ItemStatus.BORROWED);

        when(repository.save(item)).thenReturn(item);
        Item result = repository.save(item);

        verify(repository, times(1)).save(item);
        assertEquals(ItemStatus.BORROWED, result.getStatus());
    }

    @Test
    @DisplayName("Should Throw Exception When Item Not Found")
    void testGetItemById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            repository.findById(999L)
                    .orElseThrow(() -> new RuntimeException("Item not found"));
        });

        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should Get and Set Item Condition Correctly")
    void testGetSetItemCondition() {
        item.setCondition(ItemCondition.USED);
        assertEquals(ItemCondition.USED, item.getCondition());
    }

    @Test
    @DisplayName("Should Delete Item Successfully")
    void testDeleteItem() {
        doNothing().when(repository).delete(item);

        repository.delete(item);

        verify(repository, times(1)).delete(item);
    }

    @Test
    @DisplayName("Should Update Purchase Price")
    void testUpdatePurchasePrice() {
        item.setPurchasePrice(999.99);

        when(repository.save(item)).thenReturn(item);
        Item result = repository.save(item);

        verify(repository, times(1)).save(item);
        assertEquals(999.99, result.getPurchasePrice());
    }

    @Test
    @DisplayName("Should Get and Set Item Image Correctly")
    void testGetSetItemImage() {
        item.setImage("new_image.png");
        assertEquals("new_image.png", item.getImage());
    }

    @Test
    @DisplayName("Should Update Item Description")
    void testUpdateItemDescription() {
        item.setDescription("High-end gaming laptop");

        when(repository.save(item)).thenReturn(item);
        Item result = repository.save(item);

        verify(repository, times(1)).save(item);
        assertEquals("High-end gaming laptop", result.getDescription());
    }

    @Test
    @DisplayName("Should Update Item Category")
    void testUpdateItemCategory() {
        item.setCategory("Computers");

        when(repository.save(item)).thenReturn(item);
        Item result = repository.save(item);

        verify(repository, times(1)).save(item);
        assertEquals("Computers", result.getCategory());
    }

    @Test
    @DisplayName("Should Get and Set Purchase Date Correctly")
    void testGetSetPurchaseDate() {
        Date date = new Date(123456789L);
        item.setPurchaseDate(date);
        assertEquals(date, item.getPurchaseDate());
    }

    @Test
    @DisplayName("toString Should Include All Fields")
    void testToStringIncludesAllFields() {
        String result = item.toString();
        assertTrue(result.contains("id=" + item.getId()));
        assertTrue(result.contains("name='" + item.getName() + "'"));
        assertTrue(result.contains("description='" + item.getDescription() + "'"));
        assertTrue(result.contains("category='" + item.getCategory() + "'"));
        assertTrue(result.contains("status=" + item.getStatus()));
        assertTrue(result.contains("owner=" + item.getOwner()));
        assertTrue(result.contains("purchaseDate=" + item.getPurchaseDate()));
        assertTrue(result.contains("purchasePrice=" + item.getPurchasePrice()));
        assertTrue(result.contains("condition=" + item.getCondition()));
        assertTrue(result.contains("image='" + item.getImage() + "'"));
    }
}
