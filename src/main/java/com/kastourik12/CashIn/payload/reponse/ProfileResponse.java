package com.kastourik12.CashIn.payload.reponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProfileResponse {
    private String name;
    private String Credit;
    private String currency;
}
