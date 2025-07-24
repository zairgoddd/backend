package com.utp.ProyFinalWeb.model;

public enum Severidad {
    BAJA("Baja", 1),
    MODERADA("Moderada", 2),
    ALTA("Alta", 3),
    CRITICA("Crítica", 4);
    
    private final String descripcion;
    private final int nivel;
    
    Severidad(String descripcion, int nivel) {
        this.descripcion = descripcion;
        this.nivel = nivel;
    }
    
    public String getDescripcion() { return descripcion; }
    public int getNivel() { return nivel; }
    
    public static Severidad fromString(String texto) {
        for (Severidad severidad : Severidad.values()) {
            if (severidad.name().equalsIgnoreCase(texto) || 
                severidad.descripcion.equalsIgnoreCase(texto)) {
                return severidad;
            }
        }
        throw new IllegalArgumentException("Severidad no válida: " + texto);
    }
}
