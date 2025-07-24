package com.utp.ProyFinalWeb.controller;

import com.utp.ProyFinalWeb.dto.*;
import com.utp.ProyFinalWeb.model.*;
import com.utp.ProyFinalWeb.service.*;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/amenazas")
@CrossOrigin(origins = "http://localhost:4200")
public class AmenazaRestController {

    @Autowired
    private AmenazaService amenazaService;

    @GetMapping
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<List<AmenazaDTO>> listarTodasLasAmenazas() {
        List<AmenazaDTO> amenazas = amenazaService.listarTodasLasAmenazas();
        return ResponseEntity.ok(amenazas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<AmenazaDTO> obtenerAmenazaPorId(@PathVariable Integer id) {
        AmenazaDTO amenaza = amenazaService.buscarPorId(id);
        return ResponseEntity.ok(amenaza);
    }

    @PostMapping
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<AmenazaDTO> crearAmenaza(@Valid @RequestBody AmenazaDTO amenazaDTO) {
        AmenazaDTO nuevaAmenaza = amenazaService.guardarAmenaza(amenazaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAmenaza);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<AmenazaDTO> actualizarAmenaza(@PathVariable Integer id,
            @Valid @RequestBody AmenazaDTO amenazaDTO) {
        AmenazaDTO amenazaActualizada = amenazaService.actualizarAmenaza(id, amenazaDTO);
        return ResponseEntity.ok(amenazaActualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MessageResponse> eliminarAmenaza(@PathVariable Integer id) {
        amenazaService.eliminarAmenaza(id);
        return ResponseEntity.ok(new MessageResponse("Amenaza eliminada exitosamente"));
    }

    @GetMapping("/nivel-riesgo/{nivel}")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<List<AmenazaDTO>> buscarPorNivelRiesgo(@PathVariable NivelRiesgo nivel) {
        List<AmenazaDTO> amenazas = amenazaService.buscarPorNivelRiesgo(nivel);
        return ResponseEntity.ok(amenazas);
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("total", amenazaService.countAllAmenazas());
        estadisticas.put("criticas", amenazaService.contarAmenazasPorNivelRiesgo(NivelRiesgo.CRITICO));
        estadisticas.put("altas", amenazaService.contarAmenazasPorNivelRiesgo(NivelRiesgo.ALTO));
        estadisticas.put("medias", amenazaService.contarAmenazasPorNivelRiesgo(NivelRiesgo.MEDIO));
        estadisticas.put("bajas", amenazaService.contarAmenazasPorNivelRiesgo(NivelRiesgo.BAJO));
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/tipos")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<List<NivelRiesgo>> obtenerTiposNivelRiesgo() {
        return ResponseEntity.ok(Arrays.asList(NivelRiesgo.values()));
    }
}