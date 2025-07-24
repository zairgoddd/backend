package com.utp.ProyFinalWeb.dto;

import com.utp.ProyFinalWeb.model.Rol;

public class RolDTO {
    
    private Integer id;
    private String nombre;
    
    public static RolDTO fromEntity(Rol rol) {
        RolDTO dto = new RolDTO();
        dto.setId(rol.getId());
        dto.setNombre(rol.getNombre());
        return dto;
    }

    public RolDTO() { }

    public RolDTO(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }
}