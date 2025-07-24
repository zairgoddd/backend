package com.utp.ProyFinalWeb.dto;

import com.utp.ProyFinalWeb.model.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

// AmenazaDTO.java - ACTUALIZADO con validaciones
public class AmenazaDTO {

    private Integer id;

    @NotBlank(message = "El tipo de amenaza es obligatorio")
    @Size(min = 3, max = 100, message = "El tipo debe tener entre 3 y 100 caracteres")
    private String tipo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;

    @NotNull(message = "El nivel de riesgo es obligatorio")
    private NivelRiesgo nivelRiesgo;

    private LocalDate fechaDeteccion;

    // Para mapear desde la entidad
    public static AmenazaDTO fromEntity(Amenaza amenaza) {
        AmenazaDTO dto = new AmenazaDTO();
        dto.setId(amenaza.getId());
        dto.setTipo(amenaza.getTipo());
        dto.setDescripcion(amenaza.getDescripcion());
        dto.setNivelRiesgo(amenaza.getNivelRiesgo());
        dto.setFechaDeteccion(amenaza.getFechaDeteccion());
        return dto;
    }

    // Constructores, getters y setters
    public AmenazaDTO() {
    }

    public AmenazaDTO(String tipo, String descripcion, NivelRiesgo nivelRiesgo, LocalDate fechaDeteccion) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.nivelRiesgo = nivelRiesgo;
        this.fechaDeteccion = fechaDeteccion;
    }

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public NivelRiesgo getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(NivelRiesgo nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public LocalDate getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(LocalDate fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }
}