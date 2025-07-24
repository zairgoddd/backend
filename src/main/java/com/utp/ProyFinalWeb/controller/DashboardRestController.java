package com.utp.ProyFinalWeb.controller;

import com.utp.ProyFinalWeb.dto.*;
import com.utp.ProyFinalWeb.model.*;
import com.utp.ProyFinalWeb.service.*;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// DashboardRestController.java - NUEVO para estadísticas generales
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardRestController {
    
    @Autowired
    private AtaqueService ataqueService;
    
    @Autowired
    private AmenazaService amenazaService;
    
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasGenerales() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        // Estadísticas generales
        estadisticas.put("totalAtaques", ataqueService.countAllAtaques());
        estadisticas.put("totalAmenazas", amenazaService.countAllAmenazas());
        
        // Estadísticas de ataques por severidad
        Map<String, Integer> ataquesPorSeveridad = new HashMap<>();
        ataquesPorSeveridad.put("CRITICA", ataqueService.contarAtaquesPorSeveridad(Severidad.CRITICA));
        ataquesPorSeveridad.put("ALTA", ataqueService.contarAtaquesPorSeveridad(Severidad.ALTA));
        ataquesPorSeveridad.put("MODERADA", ataqueService.contarAtaquesPorSeveridad(Severidad.MODERADA));
        ataquesPorSeveridad.put("BAJA", ataqueService.contarAtaquesPorSeveridad(Severidad.BAJA));
        estadisticas.put("ataquesPorSeveridad", ataquesPorSeveridad);
        
        // Estadísticas de amenazas por nivel de riesgo
        Map<String, Integer> amenazasPorNivel = new HashMap<>();
        amenazasPorNivel.put("CRITICO", amenazaService.contarAmenazasPorNivelRiesgo(NivelRiesgo.CRITICO));
        amenazasPorNivel.put("ALTO", amenazaService.contarAmenazasPorNivelRiesgo(NivelRiesgo.ALTO));
        amenazasPorNivel.put("MEDIO", amenazaService.contarAmenazasPorNivelRiesgo(NivelRiesgo.MEDIO));
        amenazasPorNivel.put("BAJO", amenazaService.contarAmenazasPorNivelRiesgo(NivelRiesgo.BAJO));
        estadisticas.put("amenazasPorNivel", amenazasPorNivel);
        
        // Datos recientes
        List<Ataque> ataquesRecientes = ataqueService.findRecentAtaques();
        List<Amenaza> amenazasRecientes = amenazaService.findRecentAmenazas();
        
        estadisticas.put("ataquesRecientes", ataquesRecientes.stream()
            .map(AtaqueDTO::fromEntity)
            .collect(Collectors.toList()));
        estadisticas.put("amenazasRecientes", amenazasRecientes.stream()
            .map(AmenazaDTO::fromEntity)
            .collect(Collectors.toList()));
        
        return ResponseEntity.ok(estadisticas);
    }
    
    @GetMapping("/ataques-recientes")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<List<AtaqueDTO>> obtenerAtaquesRecientes() {
        List<Ataque> ataques = ataqueService.findRecentAtaques();
        List<AtaqueDTO> ataquesDTO = ataques.stream()
            .map(AtaqueDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ataquesDTO);
    }
    
    @GetMapping("/amenazas-recientes")
    @PreAuthorize("hasRole('ANALISTA') or hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<List<AmenazaDTO>> obtenerAmenazasRecientes() {
        List<Amenaza> amenazas = amenazaService.findRecentAmenazas();
        List<AmenazaDTO> amenazasDTO = amenazas.stream()
            .map(AmenazaDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(amenazasDTO);
    }
}