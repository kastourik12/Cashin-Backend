package com.kastourik12.CashIn.controllers;

import com.kastourik12.CashIn.payload.request.PayPalPaymentRequest;
import com.kastourik12.CashIn.services.PayPalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

@RestController()
@RequestMapping("api/v1/paypal")
@RequiredArgsConstructor
public class PayPalController {
    private final PayPalService payPalService; 
    @PostMapping("/pay")
    public ResponseEntity<?> createPayment(@RequestBody PayPalPaymentRequest paymentRequest) throws PayPalRESTException {

        Payment createPayment = payPalService.createPayment(paymentRequest);
        for (Links link : createPayment.getLinks()) {
            if (link.getRel().equalsIgnoreCase("approval_url")) {
                String redirectUrl = link.getHref();
                return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.LOCATION, redirectUrl).build();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment creation failed");
    }


    @GetMapping("/execute")
    public ResponseEntity<?> executePayment(@RequestHeader String paymentId, @RequestHeader String payerId)   {
        try {
            return ResponseEntity.ok(payPalService.paymentConfirmation(paymentId, payerId));
        }
        catch (PayPalRESTException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        }


}
