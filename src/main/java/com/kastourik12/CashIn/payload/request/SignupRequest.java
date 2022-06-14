package com.kastourik12.CashIn.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    @NotBlank
    private String username;
    private String password;
    @NotBlank
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    List<String> roles ;
}
