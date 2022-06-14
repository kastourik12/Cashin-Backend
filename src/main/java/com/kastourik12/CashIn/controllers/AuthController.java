package com.kastourik12.CashIn.controllers;
import com.kastourik12.CashIn.payload.request.LoginRequest;
import com.kastourik12.CashIn.payload.request.SignupRequest;
import com.kastourik12.CashIn.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		return authService.authenticateUser(loginRequest);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		return authService.saveUser(signUpRequest);
	}
	@GetMapping("accountVerification/{token}")
	public ResponseEntity<?> verifyUser(@PathVariable String token) {
		return authService.verifyUser(token);
	}
}
