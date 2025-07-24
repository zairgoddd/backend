package com.utp.ProyFinalWeb.controller;
import com.utp.ProyFinalWeb.dto.*;
import com.utp.ProyFinalWeb.model.*;
import com.utp.ProyFinalWeb.service.*;
import jakarta.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// UsuarioRestController.java
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioRestController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RolService rolService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable Integer id) {
        UsuarioDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioDTO> crearUsuario(@Valid @RequestBody RegisterRequest registerRequest) {
        UsuarioDTO nuevoUsuario = usuarioService.guardarUsuario(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@PathVariable Integer id, 
            @Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MessageResponse> eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok(new MessageResponse("Usuario eliminado exitosamente"));
    }
    
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MessageResponse> toggleEstado(@PathVariable Integer id) {
        usuarioService.toggleEstadoUsuario(id);
        return ResponseEntity.ok(new MessageResponse("Estado del usuario actualizado"));
    }
    
    @PutMapping("/{id}/rol")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MessageResponse> cambiarRol(@PathVariable Integer id, 
            @Valid @RequestBody CambiarRolRequest request) {
        usuarioService.cambiarRolUsuario(id, request.getNuevoRol());
        return ResponseEntity.ok(new MessageResponse("Rol del usuario actualizado"));
    }
    
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<RolDTO>> listarRoles() {
        List<RolDTO> roles = rolService.listarTodosLosRoles();
        return ResponseEntity.ok(roles);
    }
    
    @GetMapping("/clientes")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioDTO>> listarClientes() {
        List<Usuario> clientes = usuarioService.findAllClientUsers();
        List<UsuarioDTO> clientesDTO = clientes.stream()
            .map(UsuarioDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(clientesDTO);
    }
    
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasUsuarios() {
        List<Usuario> usuarios = usuarioService.findAllUsuarios();
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("total", usuarios.size());
        estadisticas.put("activos", usuarios.stream().mapToInt(u -> u.isEnabled() ? 1 : 0).sum());
        estadisticas.put("inactivos", usuarios.stream().mapToInt(u -> !u.isEnabled() ? 1 : 0).sum());
        
        Map<String, Long> usuariosPorRol = usuarios.stream()
            .flatMap(u -> u.getRoles().stream())
            .collect(Collectors.groupingBy(Rol::getNombre, Collectors.counting()));
        estadisticas.put("usuariosPorRol", usuariosPorRol);
        
        return ResponseEntity.ok(estadisticas);
    }
    
    // NUEVO ENDPOINT: Buscar usuarios por rol
    @GetMapping("/por-rol/{rolNombre}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioDTO>> obtenerUsuariosPorRol(@PathVariable String rolNombre) {
        List<Usuario> usuarios = usuarioService.findByRolNombre(rolNombre);
        List<UsuarioDTO> usuariosDTO = usuarios.stream()
            .map(UsuarioDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosDTO);
    }
}