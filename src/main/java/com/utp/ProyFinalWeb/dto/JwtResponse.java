package com.utp.ProyFinalWeb.dto;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String username;
    private String email;
    private String nombre;
    private String role;

    public JwtResponse(String accessToken, Integer id, String username, String email, String nombre, String role) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.role = role;
    }

    public JwtResponse() {
    }

    //Getters and Setters
    public String getToken() {  return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; } 
}