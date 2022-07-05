package com.kastourik12.CashIn.services;

import com.kastourik12.CashIn.events.PaymentEvent;
import com.kastourik12.CashIn.exception.CustomException;
import com.kastourik12.CashIn.models.EClient;
import com.kastourik12.CashIn.models.EPaymentStatus;
import com.kastourik12.CashIn.models.Payment;
import com.kastourik12.CashIn.repositories.PaymentRepository;
import com.paypal.api.payments.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;
    public void createPayment(com.paypal.api.payments.Payment createPayment) {
        com.kastourik12.CashIn.models.Payment payment = new com.kastourik12.CashIn.models.Payment();
        Transaction transaction = createPayment.getTransactions().get(0);
        payment.setStatus(EPaymentStatus.UNPAID);
        payment.setAmount(transaction.getAmount().getTotal());
        payment.setPaymentId(createPayment.getId());
        payment.setClient(EClient.PAYPAL);
        paymentRepository.save(payment);
    }

    public void updatePaymentStatus(String paymentId, EPaymentStatus status) {
        paymentRepository.updatePaymentStatus(paymentId, status);
        Payment updatedPayment = paymentRepository.findByPaymentId(paymentId).orElseThrow(()-> new CustomException("Payment not found"));
        eventPublisher.publishEvent(new PaymentEvent(updatedPayment));
    }

    public void updatePayerId(String paymentId, String payerId) {
        paymentRepository.updatePayerId(paymentId, payerId);
    }
    public ResponseEntity<?> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }
    public ResponseEntity<?> getPaymentById(Long id) {
        try {
            return ResponseEntity.ok(paymentRepository.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Payment with id " + id + " not found");
        }
    }
}
