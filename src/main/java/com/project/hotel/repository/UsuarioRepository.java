package com.project.hotel.repository;

import com.project.hotel.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    @Query(value = """
        SELECT p.CODIGO
        FROM ADMIN.USUARIOS u
        JOIN ADMIN.ROL_PERMISOS rp
          ON rp.ID_ROL = u.ID_ROL
         AND rp.ACTIVO = 'S'
        JOIN ADMIN.PERMISOS p
          ON p.ID_PERMISO = rp.ID_PERMISO
         AND p.ACTIVO = 'S'
        WHERE UPPER(u.USERNAME) = UPPER(:username)
        """, nativeQuery = true)
    List<String> findPermisosByUsername(@Param("username") String username);
}