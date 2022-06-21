package com.kastourik12.CashIn.models;

import javax.persistence.OneToOne;

public class Payout {
    private Long id;
    private Integer sum;
    private ECurrency currency;
    private EClient client;
    @OneToOne
    private CustomUser to;
}
