package com.utp.ProyFinalWeb.controller;

import com.utp.ProyFinalWeb.dto.MessageResponse;
import com.utp.ProyFinalWeb.dto.RolDTO;
import com.utp.ProyFinalWeb.service.RolService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:4200")
public class RolRestController {
    
    @Autowired
    private RolService rolService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<RolDTO>> listarRoles() {
        List<RolDTO> roles = rolService.listarTodosLosRoles();
        return ResponseEntity.ok(roles);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RolDTO> obtenerRol(@PathVariable Integer id) {
        RolDTO rol = rolService.buscarPorId(id);
        return ResponseEntity.ok(rol);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RolDTO> crearRol(@Valid @RequestBody RolDTO rolDTO) {
        RolDTO nuevoRol = rolService.crearRol(rolDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRol);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RolDTO> actualizarRol(@PathVariable Integer id, 
            @Valid @RequestBody RolDTO rolDTO) {
        RolDTO rolActualizado = rolService.actualizarRol(id, rolDTO);
        return ResponseEntity.ok(rolActualizado);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MessageResponse> eliminarRol(@PathVariable Integer id) {
        rolService.eliminarRol(id);
        return ResponseEntity.ok(new MessageResponse("Rol eliminado exitosamente"));
    }
    
    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RolDTO> obtenerRolPorNombre(@PathVariable String nombre) {
        RolDTO rol = rolService.buscarPorNombre(nombre);
        return ResponseEntity.ok(rol);
    }
}