package com.kastourik12.CashIn.models;

import com.paypal.api.payments.Amount;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    private Long id;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private ECurrency currency;


    @Enumerated(EnumType.STRING)
    private EClient client;
    private String from;

    @OneToOne
    private CustomUser to;


}
