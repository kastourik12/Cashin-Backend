package com.kastourik12.CashIn.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayPalPaymentRequest {
    Double total;
    String description;
}
