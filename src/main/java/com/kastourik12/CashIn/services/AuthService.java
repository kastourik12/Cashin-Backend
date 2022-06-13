package com.kastourik12.CashIn.services;
import com.kastourik12.CashIn.common.MailService;
import com.kastourik12.CashIn.common.NotificationEmail;
import com.kastourik12.CashIn.exception.CustomException;
import com.kastourik12.CashIn.models.CustomUser;
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
import org.springframework.beans.factory.annotation.Autowired;
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
public class AuthService {
    @Autowired
    private CustomUserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private MailService mailService;

    @Autowired
    PasswordEncoder encoder;
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
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
        CustomUser user = CustomUser.builder()
                .username(signupRequest.getUsername())
                .password(encoder.encode(signupRequest.getPassword()))
                .email(signupRequest.getEmail())
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .phone(signupRequest.getPhoneNumber())
                .enabled(false)
                .roles(new HashSet<Role>())
                .build();

        Set<String> strRoles = new HashSet<String>(signupRequest.getRoles());
        Set<Role> roles = new HashSet<>();


        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new CustomException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new CustomException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new CustomException("Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new CustomException("Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        String token =generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Cashin, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    public ResponseEntity<?> verifyUser(String verificationToken) {
        VerificationToken token = verificationTokenRepository.findByToken(verificationToken).orElseThrow(()-> new CustomException("Invalid Token"));
        String username = token.getUser().getUsername();
        CustomUser user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException("User not found with name - " + username));
        user.setEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User verified successfully!"));
    }

    private String generateVerificationToken(CustomUser user) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return verificationToken.getToken();
    }

}


