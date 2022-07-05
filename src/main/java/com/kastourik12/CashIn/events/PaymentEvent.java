package com.kastourik12.CashIn.events;

import com.kastourik12.CashIn.models.Payment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
    private final Payment payment;
    public  PaymentEvent(Payment payment) {
        super(payment);
        this.payment = payment;
    }
}
