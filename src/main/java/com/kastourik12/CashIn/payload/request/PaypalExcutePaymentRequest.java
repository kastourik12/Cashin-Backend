package com.kastourik12.CashIn.payload.request;

import lombok.Data;

@Data
public class PaypalExcutePaymentRequest {
    String payerID;
    String paymentId;
}
