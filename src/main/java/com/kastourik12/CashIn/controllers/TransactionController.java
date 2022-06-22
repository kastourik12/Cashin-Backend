package com.kastourik12.CashIn.controllers;

import com.kastourik12.CashIn.payload.request.TransactionPayload;
import com.kastourik12.CashIn.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    @GetMapping("/sent/all")
    public ResponseEntity<?> getAllSentTransactions(){
        return transactionService.getAllSentTransactions();
    }
    @GetMapping("/recived/all")
    public ResponseEntity<?> getAllReceivedTransactions(){
        return transactionService.getAllReceivedTransactions();
    }

    @PostMapping("/send")
    public ResponseEntity<?> SendToUser(@RequestBody TransactionPayload transactionPayload ){
        return transactionService.SendToUser(transactionPayload);
    }

}
