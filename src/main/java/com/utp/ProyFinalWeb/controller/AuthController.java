package com.utp.ProyFinalWeb.controller;

import com.utp.ProyFinalWeb.dto.*;
import com.utp.ProyFinalWeb.exception.*;
import com.utp.ProyFinalWeb.model.*;
import com.utp.ProyFinalWeb.repository.RolRepository;
import com.utp.ProyFinalWeb.security.*;
import com.utp.ProyFinalWeb.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword())
            );

            SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities());

            // Obtener información del usuario
            Usuario usuario = userDetails.getUsuario();
            String role = usuario.getRoles().iterator().next().getNombre();

            return ResponseEntity.ok(new JwtResponse(
                    token,
                    usuario.getId(),
                    usuario.getUsername(),
                    usuario.getEmail(),
                    usuario.getNombre(),
                    role
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(401, "Credenciales inválidas", LocalDateTime.now()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Verificar si el usuario ya existe
            if (usuarioService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "El email ya está registrado", LocalDateTime.now()));
            }

            // Usar el servicio para crear el usuario
            UsuarioDTO nuevoUsuario = usuarioService.guardarUsuario(registerRequest);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Usuario registrado exitosamente"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Error en el registro: " + e.getMessage(), LocalDateTime.now()));
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        String token = request.getHeader(JwtConfig.HEADER_STRING);

        if (token != null && token.startsWith(JwtConfig.TOKEN_PREFIX)) {
            token = token.substring(7);
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                if (username != null && !jwtUtil.isTokenExpired(token)) {
                    Usuario usuario = usuarioService.findByUsername(username);
                    return ResponseEntity.ok(new MessageResponse("Token válido"));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse(401, "Token inválido", LocalDateTime.now()));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401, "Token no encontrado o inválido", LocalDateTime.now()));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioActual(Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByUsername(username);
        return ResponseEntity.ok(UsuarioDTO.fromEntity(usuario));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(new MessageResponse("Logout exitoso"));
    }
}
