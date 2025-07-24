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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
    private VerificationCodeService verificationCodeService;

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

    // NUEVO: Endpoint para crear cuenta (registro)
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Verificar si el usuario ya existe
            if (usuarioService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "El email ya está registrado", LocalDateTime.now()));
            }

            // Crear usuario pero sin verificar aún
            Usuario usuario = new Usuario();
            usuario.setNombre(registerRequest.getNombre());
            usuario.setUsername(registerRequest.getEmail());
            usuario.setEmail(registerRequest.getEmail());
            usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            usuario.setEnabled(false); // Deshabilitado hasta verificar
            usuario.setVerificado(false); // No verificado
            usuario.setCreatedAt(LocalDateTime.now());

            // Generar código de verificación
            String codigoVerificacion = verificationCodeService.generarCodigoVerificacion();
            usuario.setCodigoVerificacion(codigoVerificacion);

            // Asignar rol CLIENTE por defecto
            Rol rolCliente = rolRepository.findByNombre("CLIENTE");
            if (rolCliente == null) {
                throw new RuntimeException("Rol CLIENTE no encontrado");
            }
            Set<Rol> roles = new HashSet<>();
            roles.add(rolCliente);
            usuario.setRoles(roles);

            // Guardar usuario
            usuarioService.save(usuario);

            // Enviar código por email
            verificationCodeService.enviarCodigoVerificacion(
                    registerRequest.getEmail(),
                    codigoVerificacion
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Usuario registrado. Revisa tu email para el código de verificación."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Error en el registro: " + e.getMessage(), LocalDateTime.now()));
        }
    }

    // NUEVO: Endpoint para verificar código
    @PostMapping("/verify-account")
    public ResponseEntity<?> verifyAccount(@RequestBody VerifyAccountRequest verifyRequest) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.findByEmail(verifyRequest.getEmail());

            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "Usuario no encontrado", LocalDateTime.now()));
            }

            Usuario usuario = usuarioOpt.get();

            // Verificar código
            if (!verifyRequest.getCodigo().equals(usuario.getCodigoVerificacion())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "Código de verificación inválido", LocalDateTime.now()));
            }

            // Activar usuario
            usuario.setVerificado(true);
            usuario.setEnabled(true);
            usuario.setCodigoVerificacion(null); // Limpiar código usado
            usuarioService.save(usuario);

            return ResponseEntity.ok(new MessageResponse("Cuenta verificada exitosamente. Ya puedes iniciar sesión."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Error en la verificación: " + e.getMessage(), LocalDateTime.now()));
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
