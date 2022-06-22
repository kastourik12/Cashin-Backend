package com.kastourik12.CashIn.payload.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class JwtResponse {
	private String token;
	private String type ;
	private String firstName;
	private String lastName;

}
