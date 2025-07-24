package com.utp.ProyFinalWeb.config;

import com.utp.ProyFinalWeb.model.Rol;
import com.utp.ProyFinalWeb.model.Usuario;
import com.utp.ProyFinalWeb.repository.*;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// DataInitializer.java - ACTUALIZADO
@Component
public class DataInitializer {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @PostConstruct
    public void init() {
        // Crear roles si no existen
        createRoleIfNotExists("ANALISTA");
        createRoleIfNotExists("ADMINISTRADOR");
        createRoleIfNotExists("CLIENTE");
        
        // Crear usuarios predeterminados
        createUserIfNotExists("analista", "analista123", "analista31@gmail.com", "Analista Martín", "ANALISTA");
        createUserIfNotExists("admin", "admin123", "administradorTI@gmail.com", "Administrador Lucas", "ADMINISTRADOR");
        createUserIfNotExists("cliente", "cliente123", "clienteJuan@gmail.com", "Cliente Juan", "CLIENTE");
    }
    
    private void createRoleIfNotExists(String roleName) {
        if (!rolRepository.existsByNombre(roleName)) {
            Rol rol = new Rol(roleName);
            rolRepository.save(rol);
            System.out.println("Rol creado: " + roleName);
        }
    }
    
    private void createUserIfNotExists(String username, String password, String email, String nombre, String roleName) {
        if (!usuarioRepository.existsByUsername(username)) {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setEmail(email);
            usuario.setNombre(nombre);
            usuario.setEnabled(true);
            usuario.setVerificado(true);
            usuario.setCreatedAt(LocalDateTime.now());
            
            Rol rol = rolRepository.findByNombre(roleName);
            Set<Rol> roles = new HashSet<>();
            roles.add(rol);
            usuario.setRoles(roles);
            
            usuarioRepository.save(usuario);
            System.out.println("Usuario creado: " + username + " con rol: " + roleName);
        }
    }
}