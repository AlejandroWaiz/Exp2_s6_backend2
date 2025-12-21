package cl.duoc.ejemplo.microservicio.security;

import cl.duoc.ejemplo.microservicio.dto.LoginRequest;
import cl.duoc.ejemplo.microservicio.security.jwt.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authManager,
            JwtService jwtService
    ) {
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest req
    ) {

        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                req.username(), req.password()
            )
        );

        var roles = auth.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .toList();

        String token = jwtService.generateToken(
                req.username(), roles
        );

        return ResponseEntity.ok(
            Map.of("token", token)
        );
    }
}
