package com.student_loan.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String category;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Temporal(TemporalType.DATE)
    @Column(name = "purchase_date")
    private Date purchaseDate;

    @Column(name = "purchase_price")
    private Double purchasePrice;

    @Enumerated(EnumType.STRING)
    private Condition condition;

    private String image;

    public enum Status {
        AVAILABLE, BORROWED, UNAVAILABLE
    }

    public enum Condition {
        NEW, LIKE_NEW, GOOD, USED, VERY_USED, DAMAGED
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }

    public Double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(Double purchasePrice) { this.purchasePrice = purchasePrice; }

    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
