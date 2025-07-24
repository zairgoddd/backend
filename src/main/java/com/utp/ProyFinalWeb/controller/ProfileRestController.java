package com.utp.ProyFinalWeb.controller;

import com.utp.ProyFinalWeb.dto.*;
import com.utp.ProyFinalWeb.model.*;
import com.utp.ProyFinalWeb.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileRestController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioDTO> obtenerPerfil(Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByUsername(username);
        return ResponseEntity.ok(UsuarioDTO.fromEntity(usuario));
    }
    
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioDTO> actualizarPerfil(@Valid @RequestBody UsuarioDTO usuarioDTO, 
            Authentication authentication) {
        String username = authentication.getName();
        Usuario usuarioActual = usuarioService.findByUsername(username);
        
        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(usuarioActual.getId(), usuarioDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }
    
    @PostMapping("/cambiar-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> cambiarPassword(@Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByUsername(username);
        
        // Verificar contraseña actual
        if (!usuarioService.verificarContraseñaActual(usuario, request.getCurrentPassword())) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("La contraseña actual es incorrecta"));
        }
        
        // Verificar que las nuevas contraseñas coincidan
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Las contraseñas no coinciden"));
        }
        
        // Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usuarioService.save(usuario);
        
        return ResponseEntity.ok(new MessageResponse("Contraseña actualizada exitosamente"));
    }
}