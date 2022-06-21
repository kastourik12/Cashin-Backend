package com.kastourik12.CashIn.models;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Transaction {
    @Id @GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    private Long id;

    private Integer sum;
    @Enumerated(EnumType.STRING)
    private ECurrency currency;

    @OneToOne
    private CustomUser from;

    @OneToOne
    private CustomUser to;

}
