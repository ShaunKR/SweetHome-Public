package shaun.sweethome.backend.web.dto;

import shaun.sweethome.backend.entity.AccountTransaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccountResponseDto {

    private Long id;
    private LocalDate transactionDate;
    private String transactionType;
    private String category;
    private String title;
    private Long amount;
    private String paymentMethod;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AccountResponseDto(AccountTransaction entity) {
        this.id = entity.getId();
        this.transactionDate = entity.getTransactionDate();
        this.transactionType = entity.getTransactionType();
        this.category = entity.getCategory();
        this.title = entity.getTitle();
        this.amount = entity.getAmount();
        this.paymentMethod = entity.getPaymentMethod();
        this.memo = entity.getMemo();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public Long getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getMemo() {
        return memo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
