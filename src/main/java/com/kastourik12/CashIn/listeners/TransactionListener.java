package com.kastourik12.CashIn.listeners;


import com.kastourik12.CashIn.events.TransactionEvent;
import com.kastourik12.CashIn.models.CustomUser;
import com.kastourik12.CashIn.repositories.CustomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransactionListener implements ApplicationListener<TransactionEvent> {
    private final CustomUserRepository userRepository;
    @Override
    public void onApplicationEvent(TransactionEvent event) {
        CustomUser receiver = event.getTransaction().getTo();
        CustomUser sender = event.getTransaction().getFrom();
        if(receiver.getCredit() != null)
        {
            receiver.setCredit(receiver.getCredit() +Integer.parseInt(event.getTransaction().getAmount()) );
        }
        else
        {
            receiver.setCredit(Integer.parseInt(event.getTransaction().getAmount()));
        }
        if(sender.getCredit() != null)
        {
            sender.setCredit(sender.getCredit() - Integer.parseInt(event.getTransaction().getAmount()));
        }
        else
        {
            sender.setCredit(-Integer.parseInt(event.getTransaction().getAmount()));
        }
        userRepository.save(receiver);
        userRepository.save(sender);

    }
}
