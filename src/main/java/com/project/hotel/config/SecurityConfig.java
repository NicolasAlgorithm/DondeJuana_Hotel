package com.project.hotel.config;

import com.project.hotel.service.DbUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final DbUserDetailsService dbUserDetailsService;

    public SecurityConfig(DbUserDetailsService dbUserDetailsService) {
        this.dbUserDetailsService = dbUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(dbUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider
    ) throws Exception {

        http
            .authenticationProvider(authenticationProvider)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/").hasAuthority("dashboard.ver")
                .requestMatchers(HttpMethod.GET, "/calendario").hasAuthority("calendario.ver")

                .requestMatchers(HttpMethod.GET, "/personas/**").hasAuthority("persona.ver")
                .requestMatchers(HttpMethod.POST, "/personas/guardar").hasAnyAuthority("persona.crear", "persona.editar")
                .requestMatchers(HttpMethod.GET, "/personas/eliminar/**").hasAuthority("persona.eliminar")

                .requestMatchers(HttpMethod.GET, "/tipos-habitacion/**").hasAuthority("tipohabitacion.ver")
                .requestMatchers(HttpMethod.POST, "/tipos-habitacion/guardar").hasAnyAuthority("tipohabitacion.crear", "tipohabitacion.editar")
                .requestMatchers(HttpMethod.GET, "/tipos-habitacion/eliminar/**").hasAuthority("tipohabitacion.eliminar")

                .requestMatchers(HttpMethod.GET, "/habitaciones").hasAuthority("habitacion.ver")
                .requestMatchers(HttpMethod.GET, "/habitaciones/nuevo").hasAuthority("habitacion.crear")
                .requestMatchers(HttpMethod.GET, "/habitaciones/editar/**").hasAuthority("habitacion.editar")
                .requestMatchers(HttpMethod.POST, "/habitaciones/guardar").hasAnyAuthority("habitacion.crear", "habitacion.editar")
                .requestMatchers(HttpMethod.POST, "/habitaciones/*/estado").hasAuthority("habitacion.estado.cambiar")
                .requestMatchers(HttpMethod.GET, "/habitaciones/eliminar/**").hasAuthority("habitacion.eliminar")

                .requestMatchers(HttpMethod.GET, "/reservas/**").hasAuthority("reserva.ver")
                .requestMatchers(HttpMethod.POST, "/reservas/guardar").hasAnyAuthority("reserva.crear", "reserva.editar")

                .requestMatchers(HttpMethod.GET, "/api/calendario/**").hasAuthority("calendario.ver")

                .requestMatchers(HttpMethod.GET, "/api/personas/**").hasAuthority("persona.ver")
                .requestMatchers(HttpMethod.POST, "/api/personas").hasAuthority("persona.crear")
                .requestMatchers(HttpMethod.PUT, "/api/personas/*").hasAuthority("persona.editar")
                .requestMatchers(HttpMethod.DELETE, "/api/personas/*").hasAuthority("persona.eliminar")

                .requestMatchers(HttpMethod.GET, "/api/tipos-habitacion/**").hasAuthority("tipohabitacion.ver")
                .requestMatchers(HttpMethod.POST, "/api/tipos-habitacion").hasAuthority("tipohabitacion.crear")
                .requestMatchers(HttpMethod.PUT, "/api/tipos-habitacion/*").hasAuthority("tipohabitacion.editar")
                .requestMatchers(HttpMethod.DELETE, "/api/tipos-habitacion/*").hasAuthority("tipohabitacion.eliminar")

                .requestMatchers(HttpMethod.GET, "/api/habitaciones/**").hasAuthority("habitacion.ver")
                .requestMatchers(HttpMethod.POST, "/api/habitaciones").hasAuthority("habitacion.crear")
                .requestMatchers(HttpMethod.PUT, "/api/habitaciones/*").hasAuthority("habitacion.editar")
                .requestMatchers(HttpMethod.PATCH, "/api/habitaciones/*/estado").hasAuthority("habitacion.estado.cambiar")
                .requestMatchers(HttpMethod.DELETE, "/api/habitaciones/*").hasAuthority("habitacion.eliminar")

                .requestMatchers(HttpMethod.GET, "/api/reservas/**").hasAuthority("reserva.ver")
                .requestMatchers(HttpMethod.POST, "/api/reservas").hasAuthority("reserva.crear")
                .requestMatchers(HttpMethod.PUT, "/api/reservas/*").hasAuthority("reserva.editar")
                .requestMatchers(HttpMethod.PATCH, "/api/reservas/*/cancelar").hasAuthority("reserva.cancelar")
                .requestMatchers(HttpMethod.PATCH, "/api/reservas/*/checkin").hasAuthority("reserva.checkin")
                .requestMatchers(HttpMethod.PATCH, "/api/reservas/*/checkout").hasAuthority("reserva.checkout")
                .requestMatchers(HttpMethod.DELETE, "/api/reservas/*").hasAuthority("reserva.eliminar")

                .anyRequest().denyAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .httpBasic(basic -> basic.realmName("DondeJuana Hotel API"))
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
