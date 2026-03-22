package com.project.hotel.service;

import com.project.hotel.entities.Rol;
import com.project.hotel.entities.Usuario;
import com.project.hotel.repository.UsuarioRepository;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public DbUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!u.isActivo()) {
            throw new DisabledException("Usuario inactivo: " + username);
        }

        Rol rol = u.getRol();
        if (rol == null) {
            throw new DisabledException("Usuario sin rol: " + username);
        }
        if (!rol.isActivo()) {
            throw new DisabledException("Rol inactivo para usuario: " + username);
        }

        String authority = "ROLE_" + rol.getNombre();

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority(authority)))
                .build();
    }
}
