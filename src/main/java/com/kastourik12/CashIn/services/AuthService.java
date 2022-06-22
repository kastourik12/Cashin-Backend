package com.kastourik12.CashIn.services;
import com.kastourik12.CashIn.common.MailService;
import com.kastourik12.CashIn.common.NotificationEmail;
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
import com.kastourik12.CashIn.security.services.UserDetailsImpl;
import com.kastourik12.CashIn.security.verficationKey.VerificationToken;
import com.kastourik12.CashIn.security.verficationKey.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ApplicationEventPublisher eventPublisher;
    private final CustomUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final VerificationTokenRepository verificationTokenRepository;

    private final PasswordEncoder encoder;
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
        CustomUser user = userRepository.findByUsernameOrEmailOrPhone(userDetails.getUsername()).orElseThrow(() -> new CustomException("User not found"));
        return ResponseEntity.ok(new JwtResponse(jwt,
                "Bearer",
                user.getFirstName(),
                user.getLastName()
                ));
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

    private String generateVerificationToken(String username) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setUsername(username);
        verificationTokenRepository.save(verificationToken);
        return verificationToken.getToken();
    }

}


