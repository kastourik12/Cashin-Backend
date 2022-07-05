package com.kastourik12.CashIn.models;

import com.paypal.api.payments.Amount;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;


@AllArgsConstructor @NoArgsConstructor @Getter @Setter
@Entity @Table(name = "payments")
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  amount;
    @Enumerated(EnumType.STRING)
    private EClient client;
    private String payerId;
    private String paymentId;
    @Enumerated(EnumType.STRING)
    private EPaymentStatus status;

    @ManyToOne
    CustomUser user;





}
