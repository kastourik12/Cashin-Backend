package com.kastourik12.CashIn.controllers;

import com.kastourik12.CashIn.models.ECurrency;
import com.kastourik12.CashIn.services.CustomUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class CustomUserController {
    private final CustomUserService userService;
    @GetMapping("/profile")
    ResponseEntity<?> getProfile(@RequestParam(required = false) ECurrency currency) throws IOException {
        return userService.getProfile(currency);
    }
}
