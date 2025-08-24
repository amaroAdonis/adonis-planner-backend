package adonis.planner.web.controller;

import adonis.planner.domain.model.User;
import adonis.planner.repository.UserRepository;
import adonis.planner.security.JwtTokenProvider;
import adonis.planner.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager am;
    private final JwtTokenProvider jwt;
    private final PasswordEncoder enc;
    private final UserRepository users;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody AuthRegister r) {
        if (users.findByEmail(r.getEmail()).isPresent()) return ResponseEntity.badRequest().build();
        User u = User.builder().name(r.getName()).email(r.getEmail()).passwordHash(enc.encode(r.getPassword())).build();
        users.save(u);
        return ResponseEntity.ok(new TokenResponse(jwt.access(u.getEmail()), jwt.refresh(u.getEmail())));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody AuthLogin r) {
        am.authenticate(new UsernamePasswordAuthenticationToken(r.getEmail(), r.getPassword()));
        return ResponseEntity.ok(new TokenResponse(jwt.access(r.getEmail()), jwt.refresh(r.getEmail())));
    }
}
