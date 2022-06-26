package com.kastourik12.CashIn.services;
import com.kastourik12.CashIn.events.UserRegistrationEvent;
import com.kastourik12.CashIn.exception.CustomException;
import com.kastourik12.CashIn.models.CustomUser;
import com.kastourik12.CashIn.models.ECurrency;
import com.kastourik12.CashIn.models.ERole;
import com.kastourik12.CashIn.models.Role;
import com.kastourik12.CashIn.payload.reponse.JwtResponse;
import com.kastourik12.CashIn.payload.reponse.MessageResponse;
import com.kastourik12.CashIn.payload.request.LoginRequest;
import com.kastourik12.CashIn.payload.request.SignupRequest;
import com.kastourik12.CashIn.repositories.CustomUserRepository;
import com.kastourik12.CashIn.repositories.RoleRepository;
import com.kastourik12.CashIn.security.jwt.JwtUtils;
import com.kastourik12.CashIn.security.refreshToken.RefreshTokenService;
import com.kastourik12.CashIn.security.verficationKey.VerificationToken;
import com.kastourik12.CashIn.security.verficationKey.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ApplicationEventPublisher eventPublisher;
    private final CustomUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final VerificationTokenRepository verificationTokenRepository;

    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder encoder;
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = refreshTokenService.generateRefreshToken(authentication);
        JwtResponse response = JwtResponse.builder()
                .authenticationToken(jwt)
                .refreshToken(refreshToken)
                .username(loginRequest.getUsername())
                .expiresAt(Date.from(Instant.now().plusMillis(jwtUtils.getJwtRefreshExpirationInMillis())))
                .build();

        return ResponseEntity.ok(response);

    }
    public ResponseEntity<?> saveUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        if(userRepository.existsByPhone(signupRequest.getPhoneNumber())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Phone is already in use!"));

        }
        CustomUser user = CustomUser.builder()
                .username(signupRequest.getUsername())
                .password(encoder.encode(signupRequest.getPassword()))
                .email(signupRequest.getEmail())
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .phone(signupRequest.getPhoneNumber())
                .enabled(false)
                .defaultCurrency(ECurrency.USD)
                .credit(0)
                .roles(new HashSet<Role>())
                .build();
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new CustomException("Error: Role is not found."));
            roles.add(userRole);
            user.setRoles(roles);
        userRepository.save(user);
        eventPublisher.publishEvent(new UserRegistrationEvent(user));
        return ResponseEntity.ok(new MessageResponse("User registered successfully! you need to activate your account ! check your email"));
    }

    public ResponseEntity<?> verifyUser(String verificationToken) {
        VerificationToken token = verificationTokenRepository.findByToken(verificationToken).orElseThrow(()-> new CustomException("Invalid Token"));
        String username = token.getUsername();
        CustomUser user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException("User not found with name - " + username));
        user.setEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User verified successfully!"));
    }

    public ResponseEntity<?> refreshAndGetAuthenticationToken(String token) {
        if(jwtUtils.validateJwtToken(token)){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwt =this.jwtUtils.generateJwtToken(authentication);
        String refreshToken = refreshTokenService.generateRefreshToken(authentication);
        JwtResponse response = JwtResponse.builder()
                .authenticationToken(jwt)
                .refreshToken(refreshToken)
                .username(authentication.getName())
                .expiresAt(Date.from(Instant.now().plusMillis(jwtUtils.getJwtRefreshExpirationInMillis())))
                .build();
        return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Invalid Token"));
    }
    private void removeAllTokensByUsername(String username){
        refreshTokenService.removeAllTokensByuser(username);
    }
    public ResponseEntity<?> signOut(String token) {
        refreshTokenService.deleteRefreshToken(token);
        removeAllTokensByUsername(jwtUtils.getUserNameFromJwtToken(token));
        return ResponseEntity.ok(new MessageResponse("User signed out successfully!"));
    }

}


