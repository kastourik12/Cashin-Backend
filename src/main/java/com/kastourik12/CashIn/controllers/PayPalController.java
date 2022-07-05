package com.kastourik12.CashIn.controllers;

import com.kastourik12.CashIn.models.EPaymentStatus;
import com.kastourik12.CashIn.payload.reponse.PayPalResponse;
import com.kastourik12.CashIn.payload.request.PayPalPaymentRequest;
import com.kastourik12.CashIn.services.PayPalService;
import com.kastourik12.CashIn.services.PaymentService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController()
@RequestMapping("api/v1/paypal")
@RequiredArgsConstructor
@Slf4j
public class PayPalController {
    private final PayPalService payPalService;
    private final PaymentService paymentService;


    @PostMapping("/pay")
    public ResponseEntity<?> createPayment(@RequestBody PayPalPaymentRequest paymentRequest) throws PayPalRESTException {

        Payment createPayment = payPalService.createPayment(paymentRequest);
        for (Links link : createPayment.getLinks()) {
            if (link.getRel().equalsIgnoreCase("approval_url")) {
                String redirectUrl = link.getHref();
                paymentService.createPayment(createPayment);
                return ResponseEntity.status(HttpStatus.OK).body(new PayPalResponse(redirectUrl));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment creation failed");
    }


    @GetMapping("/execute")
    public ResponseEntity<?> executePayment(@RequestParam String paymentId, @RequestParam String PayerID) throws PayPalRESTException {
        try {
            Payment confirmedPayment = payPalService.paymentConfirmation(paymentId, PayerID);
            if (confirmedPayment.getState().equalsIgnoreCase("approved")) {
                return ResponseEntity.status(HttpStatus.SEE_OTHER).body("Payment approved");
            }
            return ResponseEntity.ok(confirmedPayment);
        }
        catch (PayPalRESTException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        }


}
