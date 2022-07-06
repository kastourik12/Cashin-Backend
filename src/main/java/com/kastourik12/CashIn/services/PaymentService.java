package com.kastourik12.CashIn.services;

import com.kastourik12.CashIn.events.PaymentEvent;
import com.kastourik12.CashIn.exception.CustomException;
import com.kastourik12.CashIn.models.CustomUser;
import com.kastourik12.CashIn.models.EClient;
import com.kastourik12.CashIn.models.EPaymentStatus;
import com.kastourik12.CashIn.models.Payment;
import com.kastourik12.CashIn.repositories.PaymentRepository;
import com.paypal.api.payments.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final CustomUserService userService;
    private final ApplicationEventPublisher eventPublisher;
    public void createPayment(com.paypal.api.payments.Payment createPayment) {
        com.kastourik12.CashIn.models.Payment payment = new com.kastourik12.CashIn.models.Payment();
        Transaction transaction = createPayment.getTransactions().get(0);
        payment.setStatus(EPaymentStatus.UNPAID);
        payment.setAmount(Double.parseDouble(transaction.getAmount().getTotal()));
        payment.setPaymentId(createPayment.getId());
        payment.setClient(EClient.PAYPAL);
        payment.setUser(userService.getCurrentUser());
        paymentRepository.save(payment);
    }

    public void updatePaymentStatus(String paymentId, EPaymentStatus status) {
        paymentRepository.updatePaymentStatus(paymentId, status.toString());
        Payment updatedPayment = paymentRepository.findByPaymentId(paymentId).orElseThrow(()-> new CustomException("Payment not found"));
        eventPublisher.publishEvent(new PaymentEvent(updatedPayment));
    }

    public void updatePayerId(String paymentId, String payerId) {
        paymentRepository.updatePayerId(paymentId, payerId);
    }
    public ResponseEntity<?> getAllPayments() {
        CustomUser user = userService.getCurrentUser();

            List<Payment>payments = paymentRepository.findAllByUserId(user.getId());
            if(payments != null)
                return ResponseEntity.ok(payments);
            return ResponseEntity.ok("No Payments yet");
    }
    public ResponseEntity<?> getPaymentById(Long id) {
        try {
            return ResponseEntity.ok(paymentRepository.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Payment with id " + id + " not found");
        }
    }

}
