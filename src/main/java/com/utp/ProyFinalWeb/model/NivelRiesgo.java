package com.utp.ProyFinalWeb.model;

public enum NivelRiesgo {
    BAJO("Bajo", 1),
    MEDIO("Medio", 2),
    ALTO("Alto", 3),
    CRITICO("Crítico", 4);
    
    private final String descripcion;
    private final int nivel;
    
    NivelRiesgo(String descripcion, int nivel) {
        this.descripcion = descripcion;
        this.nivel = nivel;
    }
    
    public String getDescripcion() { return descripcion; }
    public int getNivel() { return nivel; }
    
    public static NivelRiesgo fromString(String texto) {
        for (NivelRiesgo nivel : NivelRiesgo.values()) {
            if (nivel.name().equalsIgnoreCase(texto) || 
                nivel.descripcion.equalsIgnoreCase(texto)) {
                return nivel;
            }
        }
        throw new IllegalArgumentException("Nivel de riesgo no válido: " + texto);
    }
}
