package com.kastourik12.CashIn.models;

import com.paypal.api.payments.Amount;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;


@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class Payment {

    private Long id;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private ECurrency currency;


    @Enumerated(EnumType.STRING)
    private EClient client;
    private String from;
    


}
