package com.student_loan.dtos;

import java.util.Date;

public class LoanAndItemDto {

    private Long loanId;
    private Long borrowerId;
    private Long lenderId;
    private Date startDate;
    private Date endDate;

    // info del item
    private Long itemId;
    private String itemName;
    private String itemDescription;

    // Constructor completo
    public LoanAndItemDto(Long loanId,Long borrowerId, Long lenderId, Date startDate, Date endDate, Long itemId, String itemName, String itemDescription) {
        this.loanId = loanId;
        this.borrowerId = borrowerId;
        this.lenderId = lenderId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
    }

    // Constructor vacío (opcional, pero recomendado para frameworks como Spring, Jackson, etc.)
    public LoanAndItemDto() {}

    // Getters y Setters
    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Long getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(Long borrowerId) {
        this.borrowerId = borrowerId;
    }

    public Long getLenderId() {
        return lenderId;
    }

    public void setLenderId(Long lenderId) {
        this.lenderId = lenderId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

}
