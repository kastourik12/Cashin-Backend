package com.kastourik12.CashIn.services;

import com.kastourik12.CashIn.exception.CustomException;
import com.kastourik12.CashIn.models.CustomUser;
import com.kastourik12.CashIn.models.ECurrency;
import com.kastourik12.CashIn.payload.reponse.ProfileResponse;
import com.kastourik12.CashIn.repositories.CustomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CustomUserService {
    private final CustomUserRepository userRepository;
    private final CurrencyService currencyService;
    public ResponseEntity<?> getProfile(ECurrency currency) throws IOException {

        CustomUser user = getCurrentUser();
        ProfileResponse profileResponse = new ProfileResponse();
                        profileResponse.setName(user.getUsername() + " " + user.getLastName());
        if((currency == null) || (currency == user.getDefaultCurrency())){

                        profileResponse.setCurrency(user.getDefaultCurrency().toString());
                        profileResponse.setCredit(user.getCredit().toString());
        }
        else {
                        switch (currency){
                            case USD:
                                profileResponse.setCurrency("USD");
                                profileResponse.setCredit(currencyService.ChangeCurrency("USD", user.getDefaultCurrency().toString(), user.getCredit().toString()));
                                break;
                            case EUR:
                                profileResponse.setCurrency("EUR");
                                profileResponse.setCredit(currencyService.ChangeCurrency("EUR", user.getDefaultCurrency().toString(), user.getCredit().toString()));
                                break;
                            case GBP:
                                profileResponse.setCurrency("GBP");
                                profileResponse.setCredit(currencyService.ChangeCurrency("GBP", user.getDefaultCurrency().toString(), user.getCredit().toString()));
                                break;
                            default:
                                throw new CustomException("Currency not found");
                        }

        }



        return ResponseEntity.ok(profileResponse);
    }
    public CustomUser getCurrentUser(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username).orElseThrow(() -> new CustomException("User not found"));
    }

}
