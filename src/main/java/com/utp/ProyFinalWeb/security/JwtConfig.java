package com.utp.ProyFinalWeb.security;

import org.springframework.stereotype.Component;

@Component
public class JwtConfig {
    // Asegúrate de que la clave tenga al menos 32 caracteres para HS256
    public static final String SECRET_KEY = "MySecretKeyForJWTTokenGenerationAndValidation2024!@#$%^&*()";
    public static final long EXPIRATION_TIME = 86400000; // 24 horas en milisegundos
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    
    public JwtConfig() { }
    
    public static String getSECRET_KEY() { return SECRET_KEY; }
    public static long getEXPIRATION_TIME() { return EXPIRATION_TIME; }
    public static String getTOKEN_PREFIX() { return TOKEN_PREFIX; }
    public static String getHEADER_STRING() { return HEADER_STRING; }
}