package com.project.hotel;

import com.project.hotel.entities.Rol;
import com.project.hotel.entities.Usuario;
import com.project.hotel.repository.UsuarioRepository;
import com.project.hotel.service.DbUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DbUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private DbUserDetailsService dbUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_lanzaUsernameNotFound_siNoExisteUsuario() {
        when(usuarioRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> dbUserDetailsService.loadUserByUsername("ghost"));
    }

    @Test
    void loadUserByUsername_lanzaDisabled_siUsuarioInactivo() {
        Usuario usuario = usuario("ana", "N", rol("ADMINISTRADOR", "S"));
        when(usuarioRepository.findByUsername("ana")).thenReturn(Optional.of(usuario));

        assertThrows(DisabledException.class,
                () -> dbUserDetailsService.loadUserByUsername("ana"));
    }

    @Test
    void loadUserByUsername_lanzaDisabled_siUsuarioNoTieneRol() {
        Usuario usuario = usuario("ana", "S", null);
        when(usuarioRepository.findByUsername("ana")).thenReturn(Optional.of(usuario));

        assertThrows(DisabledException.class,
                () -> dbUserDetailsService.loadUserByUsername("ana"));
    }

    @Test
    void loadUserByUsername_lanzaDisabled_siRolInactivo() {
        Usuario usuario = usuario("ana", "S", rol("ADMINISTRADOR", "N"));
        when(usuarioRepository.findByUsername("ana")).thenReturn(Optional.of(usuario));

        assertThrows(DisabledException.class,
                () -> dbUserDetailsService.loadUserByUsername("ana"));
    }

    @Test
    void loadUserByUsername_retornaUsuarioConRolYPermisos() {
        Usuario usuario = usuario("ana", "S", rol("ADMINISTRADOR", "S"));
        when(usuarioRepository.findByUsername("ana")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findPermisosByUsername("ana"))
                .thenReturn(List.of("RESERVA_LEER", "HABITACION_EDITAR"));

        UserDetails details = dbUserDetailsService.loadUserByUsername("ana");

        assertEquals("ana", details.getUsername());
        assertEquals("bcrypt-hash", details.getPassword());

        Set<String> authorities = details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertTrue(authorities.contains("ROLE_ADMINISTRADOR"));
        assertTrue(authorities.contains("RESERVA_LEER"));
        assertTrue(authorities.contains("HABITACION_EDITAR"));
    }

    private Usuario usuario(String username, String activo, Rol rol) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPasswordHash("bcrypt-hash");
        u.setActivo(activo);
        u.setRol(rol);
        return u;
    }

    private Rol rol(String nombre, String activo) {
        Rol r = new Rol();
        r.setNombre(nombre);
        r.setActivo(activo);
        return r;
    }
}
