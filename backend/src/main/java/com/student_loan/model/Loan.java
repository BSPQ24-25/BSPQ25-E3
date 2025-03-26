package com.student_loan.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lender_id", nullable = false)
    private User lender;

    @ManyToOne
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Temporal(TemporalType.DATE)
    @Column(name = "loan_date", nullable = false)
    private Date loanDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "estimated_return_date")
    private Date estimatedReturnDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "real_return_date")
    private Date realReturnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_status", nullable = false)
    private Status loanStatus;

    private Double rating;

    private String observations;

    public enum Status {
        IN_USE, RETURNED, DELAYED, LOST
    }

    // Empty constructor
    public Loan() {
    }

    // Constructor 
    public Loan(Long id, User lender, User borrower, Item item, Date loanDate, Date estimatedReturnDate, Date realReturnDate, Status loanStatus, Double rating, String observations) {
        this.id = id;
        this.lender = lender;
        this.borrower = borrower;
        this.item = item;
        this.loanDate = loanDate;
        this.estimatedReturnDate = estimatedReturnDate;
        this.realReturnDate = realReturnDate;
        this.loanStatus = loanStatus;
        this.rating = rating;
        this.observations = observations;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getLender() { return lender; }
    public void setLender(User lender) { this.lender = lender; }

    public User getBorrower() { return borrower; }
    public void setBorrower(User borrower) { this.borrower = borrower; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public Date getLoanDate() { return loanDate; }
    public void setLoanDate(Date loanDate) { this.loanDate = loanDate; }

    public Date getEstimatedReturnDate() { return estimatedReturnDate; }
    public void setEstimatedReturnDate(Date estimatedReturnDate) { this.estimatedReturnDate = estimatedReturnDate; }

    public Date getRealReturnDate() { return realReturnDate; }
    public void setRealReturnDate(Date realReturnDate) { this.realReturnDate = realReturnDate; }

    public Status getLoanStatus() { return loanStatus; }
    public void setLoanStatus(Status loanStatus) { this.loanStatus = loanStatus; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    // toString
    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", lender=" + lender +
                ", borrower=" + borrower +
                ", item=" + item +
                ", loanDate=" + loanDate +
                ", estimatedReturnDate=" + estimatedReturnDate +
                ", realReturnDate=" + realReturnDate +
                ", loanStatus=" + loanStatus +
                ", rating=" + rating +
                ", observations='" + observations + '\'' +
                '}';
    }
}
