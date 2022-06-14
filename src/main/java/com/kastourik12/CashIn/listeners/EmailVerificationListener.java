package com.kastourik12.CashIn.listeners;

import com.kastourik12.CashIn.common.MailService;
import com.kastourik12.CashIn.common.NotificationEmail;
import com.kastourik12.CashIn.events.UserRegistrationEvent;
import com.kastourik12.CashIn.security.verficationKey.VerificationTokenRepository;
import com.kastourik12.CashIn.security.verficationKey.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationListener implements ApplicationListener<UserRegistrationEvent> {

    private final MailService mailService;
    private final VerificationTokenService verificationTokenService;

    @Override
    public void onApplicationEvent(UserRegistrationEvent event) {
        String username = event.getUser().getUsername();
        String token = verificationTokenService.generateVerificationToken(username);
        String message = "Please, follow the link below to verify your email address: " +
                "http://localhost:8123/api/auth/accountVerification/" + token;
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
        event.getUser().getEmail(), message));

    }
}
