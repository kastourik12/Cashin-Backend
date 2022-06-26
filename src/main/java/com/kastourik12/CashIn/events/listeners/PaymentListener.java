package com.kastourik12.CashIn.events.listeners;

import com.kastourik12.CashIn.events.PaymentEvent;
import com.kastourik12.CashIn.models.CustomUser;
import com.kastourik12.CashIn.repositories.CustomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentListener implements ApplicationListener<PaymentEvent> {
    private final CustomUserRepository userRepository;
    @Override
    public void onApplicationEvent(PaymentEvent event) {
        CustomUser receiver = event.getPayment().getTo();
        if(receiver.getCredit() != null)
        {
            receiver.setCredit(receiver.getCredit() + event.getPayment().getAmount());
        }
        else
        {
            receiver.setCredit(event.getPayment().getAmount());
        }
        userRepository.save(receiver);

        System.out.println("User " + receiver.getUsername() + " has received payment " + event.getPayment().getAmount());
    }
}
