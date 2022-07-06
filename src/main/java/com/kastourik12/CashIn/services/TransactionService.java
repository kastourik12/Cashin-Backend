package com.kastourik12.CashIn.services;

import com.kastourik12.CashIn.events.TransactionEvent;
import com.kastourik12.CashIn.exception.CustomException;
import com.kastourik12.CashIn.models.CustomUser;
import com.kastourik12.CashIn.models.Transaction;
import com.kastourik12.CashIn.payload.request.TransactionRequest;
import com.kastourik12.CashIn.repositories.CustomUserRepository;
import com.kastourik12.CashIn.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CustomUserService customUserService;
    private final TransactionRepository transactionRepository;
    private final CustomUserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;
    public ResponseEntity<?> getAllSentTransactions(){
        CustomUser from = customUserService.getCurrentUser();
        List<Transaction> transactionList = transactionRepository.findAllByFromId(from.getId());
        return ResponseEntity.ok(transactionList);
    }
    public ResponseEntity<?> getAllReceivedTransactions(){

        CustomUser to = customUserService.getCurrentUser();
        List<Transaction> transactionList = transactionRepository.findAllByToId(to.getId());
        return ResponseEntity.ok(transactionList);
    }

    public ResponseEntity<?> SendToUser(TransactionRequest transactionRequest) {
        CustomUser from = customUserService.getCurrentUser();
        CustomUser to = userRepository.
                findByUsernameOrEmailOrPhone(transactionRequest.getReceiver()).
                orElseThrow(() -> new CustomException("User not found"));
        Transaction transaction = new Transaction();
                    transaction.setAmount(transactionRequest.getAmount());
                    transaction.setCurrency(transactionRequest.getCurrency());
                    transaction.setFrom(from);
                    transaction.setTo(to);
        transactionRepository.save(transaction);
        eventPublisher.publishEvent(new TransactionEvent(transaction));
        return ResponseEntity.ok(transaction);
    }
}
