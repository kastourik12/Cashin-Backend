package com.kastourik12.CashIn.events;

import com.kastourik12.CashIn.models.CustomUser;
import com.kastourik12.CashIn.models.Transaction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TransactionEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
    private final Transaction transaction;
    public  TransactionEvent(Transaction transaction) {
        super(transaction);
        this.transaction = transaction;
    }
}
