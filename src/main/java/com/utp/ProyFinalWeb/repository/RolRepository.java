package com.utp.ProyFinalWeb.repository;

import com.utp.ProyFinalWeb.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    Rol findByNombre(String nombre);
     boolean existsByNombre(String nombre);
}
