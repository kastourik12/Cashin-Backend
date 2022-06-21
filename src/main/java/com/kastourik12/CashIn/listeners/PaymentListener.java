package com.kastourik12.CashIn.listeners;

import com.kastourik12.CashIn.events.PaymentEvent;
import com.kastourik12.CashIn.models.CustomUser;
import org.springframework.context.ApplicationListener;

public class PaymentListener implements ApplicationListener<PaymentEvent> {
    @Override
    public void onApplicationEvent(PaymentEvent event) {
        CustomUser user = event.getPayment().getTo();
        if(user.getCredit() != null)
        {
            user.setCredit(user.getCredit() + event.getPayment().getAmount());
        }
        else
        {
            user.setCredit(event.getPayment().getAmount());
        }
        System.out.println("User " + user.getUsername() + " has received payment " + event.getPayment().getAmount());
    }
}
