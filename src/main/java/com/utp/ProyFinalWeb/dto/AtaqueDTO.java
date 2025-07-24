package com.utp.ProyFinalWeb.dto;

import com.utp.ProyFinalWeb.model.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

// AtaqueDTO.java - ACTUALIZADO con validaciones
public class AtaqueDTO {
    private Integer id;
    
    @NotBlank(message = "El tipo de ataque es obligatorio")
    @Size(min = 3, max = 100, message = "El tipo debe tener entre 3 y 100 caracteres")
    private String tipo;
    
    @NotBlank(message = "El vector de ataque es obligatorio")
    @Size(min = 3, max = 200, message = "El vector debe tener entre 3 y 200 caracteres")
    private String vector;
    
    @NotBlank(message = "El sistema afectado es obligatorio")
    @Size(min = 3, max = 150, message = "El sistema afectado debe tener entre 3 y 150 caracteres")
    private String sistemaAfectado;
    
    @NotNull(message = "La severidad es obligatoria")
    private Severidad severidad;
    
    private LocalDate fechaEvento;
    
    @NotNull(message = "La amenaza asociada es obligatoria")
    private Integer amenazaId;
    
    private AmenazaDTO amenaza;
    
    // Modificar el método fromEntity en AtaqueDTO para incluir la información de la amenaza completa
    public static AtaqueDTO fromEntity(Ataque ataque) {
        AtaqueDTO dto = new AtaqueDTO();
        dto.setId(ataque.getId());
        dto.setTipo(ataque.getTipo());
        dto.setVector(ataque.getVector());
        dto.setSistemaAfectado(ataque.getSistemaAfectado());
        dto.setSeveridad(ataque.getSeveridad());
        dto.setFechaEvento(ataque.getFechaEvento());

        if (ataque.getAmenaza() != null) {
            dto.setAmenazaId(ataque.getAmenaza().getId());
            dto.setAmenaza(AmenazaDTO.fromEntity(ataque.getAmenaza()));
        }

        return dto;
    }
    
    // Constructores, getters y setters
    public AtaqueDTO() {
    }

    public AtaqueDTO(String tipo, String vector, String sistemaAfectado, Severidad severidad,
            LocalDate fechaEvento, Integer amenazaId) {
        this.tipo = tipo;
        this.vector = vector;
        this.sistemaAfectado = sistemaAfectado;
        this.severidad = severidad;
        this.fechaEvento = fechaEvento;
        this.amenazaId = amenazaId;
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

    public String getVector() {
        return vector;
    }

    public void setVector(String vector) {
        this.vector = vector;
    }

    public String getSistemaAfectado() {
        return sistemaAfectado;
    }

    public void setSistemaAfectado(String sistemaAfectado) {
        this.sistemaAfectado = sistemaAfectado;
    }

    public Severidad getSeveridad() {
        return severidad;
    }

    public void setSeveridad(Severidad severidad) {
        this.severidad = severidad;
    }

    public LocalDate getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDate fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public Integer getAmenazaId() {
        return amenazaId;
    }

    public void setAmenazaId(Integer amenazaId) {
        this.amenazaId = amenazaId;
    }

    public AmenazaDTO getAmenaza() {
        return amenaza;
    }

    public void setAmenaza(AmenazaDTO amenaza) {
        this.amenaza = amenaza;
    }
}