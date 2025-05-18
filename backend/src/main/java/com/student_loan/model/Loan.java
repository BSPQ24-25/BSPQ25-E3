
package com.student_loan.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entity class representing a loan in the system. Maps to the "loans" table in the database.
 */
@Entity
@Table(name = "loans")
public class Loan {

    /**
     * The unique identifier for the loan.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The ID of the lender associated with the loan.
     */
    @Column(name = "lender_id", nullable = false)
    private Long lender;

    /**
     * The ID of the borrower associated with the loan.
     */
    @Column(name = "borrower_id", nullable = false)
    private Long borrower;

    /**
     * The ID of the item being loaned.
     */
    @Column(name = "item_id", nullable = false)
    private Long item;

    /**
     * The date when the loan was initiated.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "loan_date", nullable = false)
    private Date loanDate;

    /**
     * The estimated return date for the loaned item.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "estimated_return_date")
    private Date estimatedReturnDate;

    /**
     * The actual return date for the loaned item.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "real_return_date")
    private Date realReturnDate;

    /**
     * The status of the loan (e.g., IN_USE, RETURNED, DELAYED, LOST).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_status", nullable = false)
    private Status loanStatus;

    /**
     * The rating given for the loan transaction.
     */
    @Column(name = "rating")
    private Double rating;

    /**
     * Additional observations or comments about the loan.
     */
    private String observations;

    /**
     * Enum representing the possible statuses of a loan.
     */
    public enum Status {
        IN_USE, RETURNED, DELAYED, LOST
    }

    /**
     * Default constructor for the Loan class.
     */
    public Loan() {
    }

    /**
     * Constructor for the Loan class.
     *
     * @param id                 The unique identifier for the loan.
     * @param lender             The ID of the lender.
     * @param borrower           The ID of the borrower.
     * @param item               The ID of the item being loaned.
     * @param loanDate           The date when the loan was initiated.
     * @param estimatedReturnDate The estimated return date for the loaned item.
     * @param realReturnDate     The actual return date for the loaned item.
     * @param loanStatus         The status of the loan.
     * @param rating             The rating given for the loan transaction.
     * @param observations       Additional observations or comments about the loan.
     */
    public Loan(Long id, Long lender, Long borrower, Long item, Date loanDate, Date estimatedReturnDate, Date realReturnDate, Status loanStatus, Double rating, String observations) {
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

    /**
     * Gets the unique identifier for the loan.
     *
     * @return The unique identifier for the loan.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the loan.
     *
     * @param id The unique identifier for the loan.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the lender associated with the loan.
     *
     * @return The ID of the lender.
     */
    public Long getLender() {
        return lender;
    }

    /**
     * Sets the ID of the lender associated with the loan.
     *
     * @param lender The ID of the lender.
     */
    public void setLender(Long lender) {
        this.lender = lender;
    }

    /**
     * Gets the ID of the borrower associated with the loan.
     *
     * @return The ID of the borrower.
     */
    public Long getBorrower() {
        return borrower;
    }

    /**
     * Sets the ID of the borrower associated with the loan.
     *
     * @param borrower The ID of the borrower.
     */
    public void setBorrower(Long borrower) {
        this.borrower = borrower;
    }

    /**
     * Gets the ID of the item being loaned.
     *
     * @return The ID of the item being loaned.
     */
    public Long getItem() {
        return item;
    }

    /**
     * Sets the ID of the item being loaned.
     *
     * @param item The ID of the item being loaned.
     */
    public void setItem(Long item) {
        this.item = item;
    }

    /**
     * Gets the date when the loan was initiated.
     *
     * @return The date when the loan was initiated.
     */
    public Date getLoanDate() {
        return loanDate;
    }

    /**
     * Sets the date when the loan was initiated.
     *
     * @param loanDate The date when the loan was initiated.
     */
    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    /**
     * Gets the estimated return date for the loaned item.
     *
     * @return The estimated return date for the loaned item.
     */
    public Date getEstimatedReturnDate() {
    	if (estimatedReturnDate == null) {
    		            return new Date();
    	}
        return estimatedReturnDate;
    }

    /**
     * Sets the estimated return date for the loaned item.
     *
     * @param estimatedReturnDate The estimated return date for the loaned item.
     */
    public void setEstimatedReturnDate(Date estimatedReturnDate) {
        this.estimatedReturnDate = estimatedReturnDate;
    }

    /**
     * Gets the actual return date for the loaned item.
     *
     * @return The actual return date for the loaned item.
     */
    public Date getRealReturnDate() {
		if (realReturnDate == null) {
			return new Date();
		}
        return realReturnDate;
    }

    /**
     * Sets the actual return date for the loaned item.
     *
     * @param realReturnDate The actual return date for the loaned item.
     */
    public void setRealReturnDate(Date realReturnDate) {
        this.realReturnDate = realReturnDate;
    }

    /**
     * Gets the status of the loan.
     *
     * @return The status of the loan.
     */
    public Status getLoanStatus() {
        return loanStatus;
    }

    /**
     * Sets the status of the loan.
     *
     * @param loanStatus The status of the loan.
     */
    public void setLoanStatus(Status loanStatus) {
        this.loanStatus = loanStatus;
    }

    /**
     * Gets the rating given for the loan transaction.
     *
     * @return The rating given for the loan transaction.
     */
    public Double getRating() {
        return rating;
    }

    /**
     * Sets the rating given for the loan transaction.
     *
     * @param rating The rating given for the loan transaction.
     */
    public void setRating(Double rating) {
        this.rating = rating;
    }

    /**
     * Gets additional observations or comments about the loan.
     *
     * @return Additional observations or comments about the loan.
     */
    public String getObservations() {
        return observations;
    }

    /**
     * Sets additional observations or comments about the loan.
     *
     * @param observations Additional observations or comments about the loan.
     */
    public void setObservations(String observations) {
        this.observations = observations;
    }

    /**
     * Returns a string representation of the loan.
     *
     * @return A string representation of the loan.
     */
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

