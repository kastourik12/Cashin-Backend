package com.kastourik12.CashIn.controllers;
import com.kastourik12.CashIn.payload.request.LoginRequest;
import com.kastourik12.CashIn.payload.request.SignupRequest;
import com.kastourik12.CashIn.security.refreshToken.RefreshToken;
import com.kastourik12.CashIn.security.refreshToken.RefreshTokenService;
import com.kastourik12.CashIn.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600)
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

	@GetMapping("/refresh")
	public ResponseEntity<?> refreshAndGetAuthenticationToken(@RequestBody String token) {
		return authService.refreshAndGetAuthenticationToken(token);
	}

	@PostMapping("/signout")
	public ResponseEntity<?> signOut(@RequestBody String token) {
		return authService.signOut(token);
	}
}
