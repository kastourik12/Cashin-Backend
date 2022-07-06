package com.kastourik12.CashIn.controllers;

import com.kastourik12.CashIn.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllPayments(){
        return this.paymentService.getAllPayments();
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id){
        return this.paymentService.getPaymentById(id);
    }

}
