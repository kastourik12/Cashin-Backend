package com.kastourik12.CashIn.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.OneToOne;
import java.time.Instant;

public class Payout {
    private Long id;
    private Integer sum;
    private ECurrency currency;
    private EClient client;
    @OneToOne
    private CustomUser to;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;
}
