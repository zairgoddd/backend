package com.utp.ProyFinalWeb.service;

import com.utp.ProyFinalWeb.dto.RolDTO;
import com.utp.ProyFinalWeb.exception.ResourceNotFoundException;
import com.utp.ProyFinalWeb.model.Rol;
import com.utp.ProyFinalWeb.repository.RolRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RolService {
    
    @Autowired
    private RolRepository rolRepository;
    
    @Transactional(readOnly = true)
    public List<RolDTO> listarTodosLosRoles() {
        return rolRepository.findAll().stream()
                .map(RolDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public RolDTO buscarPorId(Integer id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
        return RolDTO.fromEntity(rol);
    }
    
    @Transactional(readOnly = true)
    public RolDTO buscarPorNombre(String nombre) {
        Rol rol = rolRepository.findByNombre(nombre);
        if (rol == null) {
            throw new ResourceNotFoundException("Rol no encontrado con nombre: " + nombre);
        }
        return RolDTO.fromEntity(rol);
    }
    
    @Transactional
    public RolDTO crearRol(RolDTO rolDTO) {
        if (rolRepository.existsByNombre(rolDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre");
        }
        
        Rol rol = new Rol();
        rol.setNombre(rolDTO.getNombre().toUpperCase());
        
        Rol savedRol = rolRepository.save(rol);
        return RolDTO.fromEntity(savedRol);
    }
    
    @Transactional
    public RolDTO actualizarRol(Integer id, RolDTO rolDTO) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
        
        // Verificar que no exista otro rol con el mismo nombre
        Rol existingRol = rolRepository.findByNombre(rolDTO.getNombre());
        if (existingRol != null && !existingRol.getId().equals(id)) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre");
        }
        
        rol.setNombre(rolDTO.getNombre().toUpperCase());
        
        Rol updatedRol = rolRepository.save(rol);
        return RolDTO.fromEntity(updatedRol);
    }
    
    @Transactional
    public void eliminarRol(Integer id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
        
        // Verificar que no sea un rol del sistema (ADMIN, ANALISTA, CLIENTE)
        if (rol.getNombre().equals("ADMINISTRADOR") || 
            rol.getNombre().equals("ANALISTA") || 
            rol.getNombre().equals("CLIENTE")) {
            throw new IllegalArgumentException("No se puede eliminar un rol del sistema");
        }
        
        rolRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean existeRolPorNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }
}