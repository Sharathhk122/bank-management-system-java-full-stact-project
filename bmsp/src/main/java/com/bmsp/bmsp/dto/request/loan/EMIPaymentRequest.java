package com.bmsp.bmsp.dto.request.loan;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EMIPaymentRequest {
    @NotNull(message = "Installment number is required")
    private Integer installmentNumber;
}