package com.utp.ProyFinalWeb.dto;

import com.utp.ProyFinalWeb.model.Usuario;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class UsuarioDTO {
    private Integer id;
    private String nombre;
    private String username;
    private String email;
    private String telefono;
    private boolean enabled;
    private LocalDateTime createdAt;
    private Set<RolDTO> roles;
    
    public static UsuarioDTO fromEntity(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
        dto.setEnabled(usuario.isEnabled());
        dto.setCreatedAt(usuario.getCreatedAt());
        dto.setRoles(usuario.getRoles().stream()
            .map(RolDTO::fromEntity)
            .collect(Collectors.toSet()));
        return dto;
    }
    
    //Constructores, Getters and Setters

    public UsuarioDTO() {
    }

    public UsuarioDTO(Integer id, String nombre, String username, String email, String telefono, boolean enabled, LocalDateTime createdAt, Set<RolDTO> roles) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.email = email;
        this.telefono = telefono;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.roles = roles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<RolDTO> getRoles() {
        return roles;
    }

    public void setRoles(Set<RolDTO> roles) {
        this.roles = roles;
    }
}