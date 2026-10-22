package shaun.sweethome.backend.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AccountRequestDto {

    @NotNull
    private LocalDate transactionDate;

    @NotBlank
    private String transactionType;

    @NotBlank
    private String category;

    @NotBlank
    private String title;

    @NotNull
    @Min(1)
    private Long amount;

    @NotBlank
    private String paymentMethod;

    private String memo;

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
}
