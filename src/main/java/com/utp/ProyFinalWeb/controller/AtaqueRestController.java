package com.utp.ProyFinalWeb.controller;

import com.utp.ProyFinalWeb.dto.*;
import com.utp.ProyFinalWeb.model.*;
import com.utp.ProyFinalWeb.service.*;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// AtaqueRestController.java
@RestController
@RequestMapping("/api/ataques")
@CrossOrigin(origins = "http://localhost:4200")
public class AtaqueRestController {

    @Autowired
    private AtaqueService ataqueService;

    @GetMapping
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<List<AtaqueDTO>> listarTodosLosAtaques() {
        List<AtaqueDTO> ataques = ataqueService.listarTodosLosAtaques();
        return ResponseEntity.ok(ataques);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<AtaqueDTO> obtenerAtaquePorId(@PathVariable Integer id) {
        AtaqueDTO ataque = ataqueService.buscarPorId(id);
        return ResponseEntity.ok(ataque);
    }

    @PostMapping
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<AtaqueDTO> crearAtaque(@Valid @RequestBody AtaqueDTO ataqueDTO) {
        AtaqueDTO nuevoAtaque = ataqueService.guardarAtaque(ataqueDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAtaque);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<AtaqueDTO> actualizarAtaque(@PathVariable Integer id,
            @Valid @RequestBody AtaqueDTO ataqueDTO) {
        AtaqueDTO ataqueActualizado = ataqueService.actualizarAtaque(id, ataqueDTO);
        return ResponseEntity.ok(ataqueActualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MessageResponse> eliminarAtaque(@PathVariable Integer id) {
        ataqueService.eliminarAtaque(id);
        return ResponseEntity.ok(new MessageResponse("Ataque eliminado exitosamente"));
    }

    @GetMapping("/amenaza/{amenazaId}")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<List<AtaqueDTO>> buscarPorAmenaza(@PathVariable Integer amenazaId) {
        List<AtaqueDTO> ataques = ataqueService.buscarPorAmenazaId(amenazaId);
        return ResponseEntity.ok(ataques);
    }

    @GetMapping("/severidad/{severidad}")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<List<AtaqueDTO>> buscarPorSeveridad(@PathVariable Severidad severidad) {
        List<AtaqueDTO> ataques = ataqueService.buscarPorSeveridad(severidad);
        return ResponseEntity.ok(ataques);
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("total", ataqueService.countAllAtaques());
        estadisticas.put("criticos", ataqueService.contarAtaquesPorSeveridad(Severidad.CRITICA));
        estadisticas.put("altos", ataqueService.contarAtaquesPorSeveridad(Severidad.ALTA));
        estadisticas.put("moderados", ataqueService.contarAtaquesPorSeveridad(Severidad.MODERADA));
        estadisticas.put("bajos", ataqueService.contarAtaquesPorSeveridad(Severidad.BAJA));
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/tipos-severidad")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<List<Severidad>> obtenerTiposSeveridad() {
        return ResponseEntity.ok(Arrays.asList(Severidad.values()));
    }
}
