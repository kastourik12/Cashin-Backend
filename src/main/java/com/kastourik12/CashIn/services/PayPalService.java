package com.kastourik12.CashIn.services;


import com.kastourik12.CashIn.payload.request.PayPalPaymentRequest;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;



@Service
@Slf4j
@RequiredArgsConstructor
public class PayPalService {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    private String cancelUrl="http://localhost:4200/";
    private String successUrl="http://localhost:4200/";
    private final APIContext apiContext;

    public Payment createPayment(PayPalPaymentRequest paypalPaymentRequest) throws PayPalRESTException {
        String currency = "USD";
        String intent = "sale";
        String method = "PAYPAL";
        Amount amount = new Amount();
        amount.setCurrency(currency);
        Double total = new BigDecimal(paypalPaymentRequest.getTotal()).setScale(2, RoundingMode.HALF_UP).doubleValue();
        amount.setTotal(String.format("%.2f", total));
        Transaction transaction = new Transaction();
        transaction.setDescription(paypalPaymentRequest.getDescription());
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method.toString());

        Payment payment = new Payment();
        payment.setIntent(intent.toString());
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();

        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);
        logger.info("Payment created successfully"+ payment.toString());
        return payment.create(apiContext);
    }
    public Payment paymentConfirmation(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }
}
