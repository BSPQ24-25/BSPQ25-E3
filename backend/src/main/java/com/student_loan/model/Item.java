
package com.student_loan.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entity class representing an item in the system. Maps to the "items" table in the database.
 */
@Entity
@Table(name = "items")
public class Item {

    /**
     * The unique identifier for the item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the item.
     */
    private String name;

    /**
     * A description of the item.
     */
    private String description;

    /**
     * The category of the item.
     */
    private String category;

    /**
     * The status of the item (e.g., AVAILABLE, BORROWED, UNAVAILABLE).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ItemStatus status;

    /**
     * The ID of the owner of the item.
     */
    @Column(name = "owner_id", nullable = false)
    private Long owner;

    /**
     * The purchase date of the item.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "purchase_date")
    private Date purchaseDate;

    /**
     * The purchase price of the item.
     */
    @Column(name = "purchase_price")
    private Double purchasePrice;

    /**
     * The condition of the item (e.g., NEW, LIKE_NEW, GOOD, etc.).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "`condition`")
    private ItemCondition condition;

    /**
     * The URL or path to the image of the item.
     */
    private String image;

    /**
     * Enum representing the possible statuses of an item.
     */
    public enum ItemStatus {
        AVAILABLE, BORROWED, UNAVAILABLE
    }

    /**
     * Enum representing the possible conditions of an item.
     */
    public enum ItemCondition {
        NEW, LIKE_NEW, GOOD, USED, VERY_USED, DAMAGED
    }

    /**
     * Default constructor for the Item class.
     */
    public Item() {
    }

    /**
     * Constructor for the Item class.
     *
     * @param id            The unique identifier for the item.
     * @param name          The name of the item.
     * @param description   A description of the item.
     * @param category      The category of the item.
     * @param status        The status of the item.
     * @param owner         The ID of the owner of the item.
     * @param purchaseDate  The purchase date of the item.
     * @param purchasePrice The purchase price of the item.
     * @param condition     The condition of the item.
     * @param image         The URL or path to the image of the item.
     */
    public Item(Long id, String name, String description, String category, ItemStatus status, Long owner, Date purchaseDate, Double purchasePrice, ItemCondition condition, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.status = status;
        this.owner = owner;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
        this.condition = condition;
        this.image = image;
    }

    // Getters & Setters

    /**
     * Gets the unique identifier for the item.
     *
     * @return The unique identifier for the item.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the item.
     *
     * @param id The unique identifier for the item.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the item.
     *
     * @return The name of the item.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the item.
     *
     * @param name The name of the item.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the item.
     *
     * @return The description of the item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the item.
     *
     * @param description The description of the item.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the category of the item.
     *
     * @return The category of the item.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the item.
     *
     * @param category The category of the item.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the status of the item.
     *
     * @return The status of the item.
     */
    public ItemStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the item.
     *
     * @param status The status of the item.
     */
    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    /**
     * Gets the ID of the owner of the item.
     *
     * @return The ID of the owner of the item.
     */
    public Long getOwner() {
        return owner;
    }

    /**
     * Sets the ID of the owner of the item.
     *
     * @param owner The ID of the owner of the item.
     */
    public void setOwner(Long owner) {
        this.owner = owner;
    }

    /**
     * Gets the purchase date of the item.
     *
     * @return The purchase date of the item.
     */
    public Date getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * Sets the purchase date of the item.
     *
     * @param purchaseDate The purchase date of the item.
     */
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * Gets the purchase price of the item.
     *
     * @return The purchase price of the item.
     */
    public Double getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Sets the purchase price of the item.
     *
     * @param purchasePrice The purchase price of the item.
     */
    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    /**
     * Gets the condition of the item.
     *
     * @return The condition of the item.
     */
    public ItemCondition getCondition() {
        return condition;
    }

    /**
     * Sets the condition of the item.
     *
     * @param condition The condition of the item.
     */
    public void setCondition(ItemCondition condition) {
        this.condition = condition;
    }

    /**
     * Gets the URL or path to the image of the item.
     *
     * @return The URL or path to the image of the item.
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the URL or path to the image of the item.
     *
     * @param image The URL or path to the image of the item.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Returns a string representation of the item.
     *
     * @return A string representation of the item.
     */
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", status=" + status +
                ", owner=" + owner +
                ", purchaseDate=" + purchaseDate +
                ", purchasePrice=" + purchasePrice +
                ", condition=" + condition +
                ", image='" + image + '\'' +
                '}';
    }
}
