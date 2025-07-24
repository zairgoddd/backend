package com.utp.ProyFinalWeb.service;

import com.utp.ProyFinalWeb.dto.*;
import com.utp.ProyFinalWeb.exception.ResourceNotFoundException;
import com.utp.ProyFinalWeb.model.*;
import com.utp.ProyFinalWeb.repository.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return UsuarioDTO.fromEntity(usuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public UsuarioDTO guardarUsuario(RegisterRequest registerRequest) {
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setUsername(registerRequest.getEmail());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setEnabled(true);
        usuario.setVerificado(true);
        usuario.setCreatedAt(LocalDateTime.now());

        // Asignar rol CLIENTE por defecto
        Rol rolCliente = rolRepository.findByNombre("CLIENTE");
        if (rolCliente == null) {
            throw new RuntimeException("Rol CLIENTE no encontrado");
        }
        Set<Rol> roles = new HashSet<>();
        roles.add(rolCliente);
        usuario.setRoles(roles);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return UsuarioDTO.fromEntity(savedUsuario);
    }

    @Transactional
    public UsuarioDTO actualizarUsuario(Integer id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Verificar duplicados excluyendo el usuario actual
        Optional<Usuario> existingUser = usuarioRepository.findByUsername(usuarioDTO.getUsername());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        Optional<Usuario> existingEmail = usuarioRepository.findByEmail(usuarioDTO.getEmail());
        if (existingEmail.isPresent() && !existingEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setTelefono(usuarioDTO.getTelefono());

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return UsuarioDTO.fromEntity(updatedUsuario);
    }

// Agregar este método actualizado a tu UsuarioService existente
    @Transactional
    public Usuario save(Usuario usuario) {
        // Solo verificar duplicados si el usuario no tiene ID (es nuevo)
        if (usuario.getId() == null) {
            if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
                throw new IllegalArgumentException("El nombre de usuario ya existe");
            }
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
            validarDatosUsuario(usuario);
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void toggleEstadoUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        usuario.setEnabled(!usuario.isEnabled());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarRolUsuario(Integer id, String nuevoRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        Rol rol = rolRepository.findByNombre(nuevoRol);
        if (rol == null) {
            throw new ResourceNotFoundException("Rol no encontrado: " + nuevoRol);
        }
        usuario.getRoles().clear();
        usuario.getRoles().add(rol);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAllClientUsers() {
        return usuarioRepository.findByRoles_Nombre("CLIENTE");
    }

    @Transactional(readOnly = true)
    public Page<Usuario> findAllClientUsers(Pageable pageable) {
        return usuarioRepository.findByRoles_Nombre("CLIENTE", pageable);
    }

    @Transactional(readOnly = true)
    public boolean verificarContraseñaActual(Usuario usuario, String contraseñaActual) {
        return passwordEncoder.matches(contraseñaActual, usuario.getPassword());
    }

    @Transactional(readOnly = true)
    public List<Rol> findAllRoles() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<RolDTO> listarTodosLosRoles() {
        return rolRepository.findAll().stream()
                .map(RolDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void validarDatosUsuario(Usuario usuario) {
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!usuario.getEmail().matches(emailRegex)) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> findByRolNombre(String rolNombre) {
        return usuarioRepository.findByRoles_Nombre(rolNombre);
    }
}
