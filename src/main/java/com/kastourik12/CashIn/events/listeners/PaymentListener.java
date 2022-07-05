package com.kastourik12.CashIn.events.listeners;

import com.kastourik12.CashIn.events.PaymentEvent;
import com.kastourik12.CashIn.models.CustomUser;
import com.kastourik12.CashIn.models.EPaymentStatus;
import com.kastourik12.CashIn.models.Payment;
import com.kastourik12.CashIn.repositories.CustomUserRepository;
import com.kastourik12.CashIn.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentListener implements ApplicationListener<PaymentEvent> {
    private final CustomUserRepository userRepository;
    private final PaymentRepository paymentRepository;
    @Override
    public void onApplicationEvent(PaymentEvent event) {
        if(event.getPayment().getStatus().equals(EPaymentStatus.PAID))
        {
            CustomUser receiver = event.getPayment().getUser();
            if(receiver.getCredit() != null)
            {
                receiver.setCredit(receiver.getCredit() + Integer.parseInt(event.getPayment().getAmount()));
                userRepository.save(receiver);
            }
            else
            {
                receiver.setCredit(Integer.parseInt(event.getPayment().getAmount()));
                userRepository.save(receiver);
            }
        }
        else
        if (event.getPayment().getStatus().equals(EPaymentStatus.CANCELLED))
        {
            Payment payment = event.getPayment();
            paymentRepository.delete(payment);
            System.out.println("Payment was not successful");
        }
    }
}
