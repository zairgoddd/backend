package com.utp.ProyFinalWeb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class VerifyAccountRequest {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "El código es obligatorio")
    private String codigo;
    
    // Constructores
    public VerifyAccountRequest() {}
    
    public VerifyAccountRequest(String email, String codigo) {
        this.email = email;
        this.codigo = codigo;
    }
    
    // Getters y Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}