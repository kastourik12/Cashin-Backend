package com.kastourik12.CashIn.payload.request;

import com.kastourik12.CashIn.models.ECurrency;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TransactionPayload {
    @NotBlank
    private String receiver;
    @NotBlank
    private String amount;
    @NotBlank
    private ECurrency currency;
}
