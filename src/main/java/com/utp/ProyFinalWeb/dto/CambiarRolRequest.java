package com.utp.ProyFinalWeb.dto;

// CambiarRolRequest.java
public class CambiarRolRequest {
    private String nuevoRol;

    public CambiarRolRequest() {
    }

    public CambiarRolRequest(String nuevoRol) {
        this.nuevoRol = nuevoRol;
    }

    public String getNuevoRol() {
        return nuevoRol;
    }

    public void setNuevoRol(String nuevoRol) {
        this.nuevoRol = nuevoRol;
    }
}