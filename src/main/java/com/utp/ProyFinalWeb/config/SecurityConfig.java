package com.utp.ProyFinalWeb.config;

import com.utp.ProyFinalWeb.security.*;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

// SecurityConfig.java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    // APIs públicas
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // APIs protegidas - Amenazas
                    .requestMatchers(HttpMethod.GET, "/api/amenazas/**").hasAnyRole("ANALISTA", "ADMINISTRADOR", "CLIENTE")
                    .requestMatchers(HttpMethod.POST, "/api/amenazas/**").hasAnyRole("ANALISTA", "ADMINISTRADOR")
                    .requestMatchers(HttpMethod.PUT, "/api/amenazas/**").hasAnyRole("ANALISTA", "ADMINISTRADOR")
                    .requestMatchers(HttpMethod.DELETE, "/api/amenazas/**").hasRole("ADMINISTRADOR")
                    // APIs protegidas - Ataques
                    .requestMatchers(HttpMethod.GET, "/api/ataques/**").hasAnyRole("ANALISTA", "ADMINISTRADOR", "CLIENTE")
                    .requestMatchers(HttpMethod.POST, "/api/ataques/**").hasAnyRole("ANALISTA", "ADMINISTRADOR")
                    .requestMatchers(HttpMethod.PUT, "/api/ataques/**").hasAnyRole("ANALISTA", "ADMINISTRADOR")
                    .requestMatchers(HttpMethod.DELETE, "/api/ataques/**").hasRole("ADMINISTRADOR")
                    // APIs protegidas - Usuarios (solo ADMIN)
                    .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")
                    // APIs protegidas - Dashboard (todos los roles autenticados)
                    .requestMatchers("/api/dashboard/**").hasAnyRole("ANALISTA", "ADMINISTRADOR", "CLIENTE")
                    // APIs protegidas - Perfil (usuario autenticado)
                    .requestMatchers("/api/profile/**").authenticated()
                    .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
