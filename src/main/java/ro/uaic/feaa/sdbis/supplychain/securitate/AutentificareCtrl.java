package ro.uaic.feaa.sdbis.supplychain.securitate;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Controller REST pentru Autentificare Enterprise bazata pe JWT.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autentificare", description = "Autentificare si administrare conturi utilizatori")
public class AutentificareCtrl {

    private static final Logger log = LoggerFactory.getLogger(AutentificareCtrl.class);

    private final UserAccountRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Value("${security.seed-default-users:false}")
    private boolean seedDefaultUsers = false;

    public AutentificareCtrl(UserAccountRepository userRepo,
                             PasswordEncoder passwordEncoder,
                             JwtTokenService jwtTokenService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @PostConstruct
    public void initDefaultUsers() {
        if (!seedDefaultUsers) {
            log.info("[Security] Default user seeding is disabled (security.seed-default-users=false).");
            return;
        }
        // Asigura utilizatori impliciti doar daca baza de date este complet goala (first boot)
        if (userRepo.count() == 0) {
            createDefaultUserIfNotExists("admin", "admin123", "admin@supplychain.ro", "Administrator Sistem", "ROLE_ADMIN");
            createDefaultUserIfNotExists("achizitor", "achizitor123", "achizitii@supplychain.ro", "Referent Achizitii", "ROLE_BUYER");
            createDefaultUserIfNotExists("inspector", "inspector123", "calitate@supplychain.ro", "Inspector Calitate", "ROLE_QUALITY_INSPECTOR");
            createDefaultUserIfNotExists("contabil", "contabil123", "financiar@supplychain.ro", "Contabil Sef", "ROLE_ACCOUNTANT");
            createDefaultUserIfNotExists("gestionar", "gestionar123", "depozit@supplychain.ro", "Gestionar Depozit", "ROLE_WAREHOUSE_KEEPER");
        }
    }

    private void createDefaultUserIfNotExists(String username, String rawPassword, String email, String fullName, String role) {
        if (!userRepo.existsByUsername(username)) {
            UserAccountEntity user = new UserAccountEntity(
                    UUID.randomUUID(),
                    username,
                    passwordEncoder.encode(rawPassword),
                    email,
                    fullName,
                    role
            );
            userRepo.save(user);
            log.info("[Auth Init] Utilizator creat: '{}' cu rol '{}'", username, role);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        Optional<UserAccountEntity> userOpt = userRepo.findByUsername(request.username().trim());
        if (userOpt.isEmpty()) {
            throw new BadCredentialsException("Credentiale invalide.");
        }

        UserAccountEntity user = userOpt.get();
        if (!user.isActiv()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Contul de utilizator este inactiv.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Credentiale invalide.");
        }

        String token = jwtTokenService.generateToken(user.getUsername(), user.getRol(), user.getId().toString());

        AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                86400,
                user.getUsername(),
                user.getRol(),
                user.getId(),
                user.getEmail(),
                user.getFullName()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
        if (userRepo.existsByUsername(request.username().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Numele de utilizator este deja folosit.");
        }

        if (userRepo.existsByEmail(request.email().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Adresa de email este deja inregistrata.");
        }

        String normalizedRole = request.role().trim().toUpperCase();
        if (!normalizedRole.startsWith("ROLE_")) {
            normalizedRole = "ROLE_" + normalizedRole;
        }

        // FIX SEC-F01: Whitelist (Allowlist) strict pentru auto-inregistrare publica
        Set<String> publicRoles = Set.of("ROLE_BUYER", "ROLE_SUPPLIER", "ROLE_FURNIZOR");
        if (!publicRoles.contains(normalizedRole)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth != null && auth.isAuthenticated() && auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            if (!isAdmin) {
                throw new AccessDeniedException("Inregistrarea conturilor cu rolul " + normalizedRole + " este restrictionata si necesita privilegii administrative prealabile.");
            }
        }

        UserAccountEntity newUser = new UserAccountEntity(
                UUID.randomUUID(),
                request.username().trim(),
                passwordEncoder.encode(request.password()),
                request.email().trim(),
                request.fullName().trim(),
                normalizedRole
        );

        userRepo.save(newUser);

        String token = jwtTokenService.generateToken(newUser.getUsername(), newUser.getRol(), newUser.getId().toString());

        AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                86400,
                newUser.getUsername(),
                newUser.getRol(),
                newUser.getId(),
                newUser.getEmail(),
                newUser.getFullName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> me(Principal principal) {
        if (principal == null) {
            throw new BadCredentialsException("Nu sunteti autentificat.");
        }

        Optional<UserAccountEntity> userOpt = userRepo.findByUsername(principal.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(new UserMeResponse(null, principal.getName(), null, null, null));
        }

        UserAccountEntity user = userOpt.get();
        return ResponseEntity.ok(new UserMeResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRol()
        ));
    }
}

