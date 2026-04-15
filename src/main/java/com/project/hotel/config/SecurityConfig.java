package com.project.hotel.config;

import com.project.hotel.service.DbUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
public class SecurityConfig {

    private final DbUserDetailsService dbUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(DbUserDetailsService dbUserDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.dbUserDetailsService = dbUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider
    ) throws Exception {

        http
            .securityMatcher("/api/**")
            .authenticationProvider(authenticationProvider)
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/token").permitAll()

                .requestMatchers(HttpMethod.GET, "/api/calendario/**")
                    .hasAnyAuthority("calendario.ver", "ROLE_ADMINISTRADOR", "ROLE_RECEPCIONISTA")

                .requestMatchers(HttpMethod.GET, "/api/personas/**").hasAuthority("persona.ver")
                .requestMatchers(HttpMethod.POST, "/api/personas").hasAuthority("persona.crear")
                .requestMatchers(HttpMethod.PUT, "/api/personas/*").hasAuthority("persona.editar")
                .requestMatchers(HttpMethod.DELETE, "/api/personas/*").hasAuthority("persona.eliminar")

                .requestMatchers(HttpMethod.GET, "/api/tipos-habitacion/**")
                    .hasAnyAuthority("tipohabitacion.ver", "ROLE_ADMINISTRADOR", "ROLE_RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST, "/api/tipos-habitacion").hasAuthority("tipohabitacion.crear")
                .requestMatchers(HttpMethod.PUT, "/api/tipos-habitacion/*").hasAuthority("tipohabitacion.editar")
                .requestMatchers(HttpMethod.DELETE, "/api/tipos-habitacion/*").hasAuthority("tipohabitacion.eliminar")

                .requestMatchers(HttpMethod.GET, "/api/habitaciones/**")
                    .hasAnyAuthority("habitacion.ver", "ROLE_ADMINISTRADOR", "ROLE_RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST, "/api/habitaciones").hasAuthority("habitacion.crear")
                .requestMatchers(HttpMethod.PUT, "/api/habitaciones/*").hasAuthority("habitacion.editar")
                .requestMatchers(HttpMethod.PATCH, "/api/habitaciones/*/estado").hasAuthority("habitacion.estado.cambiar")
                .requestMatchers(HttpMethod.DELETE, "/api/habitaciones/*").hasAuthority("habitacion.eliminar")

                .requestMatchers(HttpMethod.GET, "/api/reservas/**")
                    .hasAnyAuthority("reserva.ver", "ROLE_ADMINISTRADOR", "ROLE_RECEPCIONISTA")
                .requestMatchers(HttpMethod.POST, "/api/reservas").hasAuthority("reserva.crear")
                .requestMatchers(HttpMethod.PUT, "/api/reservas/*").hasAuthority("reserva.editar")
                .requestMatchers(HttpMethod.PATCH, "/api/reservas/*/cancelar").hasAuthority("reserva.cancelar")
                .requestMatchers(HttpMethod.PATCH, "/api/reservas/*/checkin").hasAuthority("reserva.checkin")
                .requestMatchers(HttpMethod.PATCH, "/api/reservas/*/checkout").hasAuthority("reserva.checkout")
                .requestMatchers(HttpMethod.DELETE, "/api/reservas/*").hasAuthority("reserva.eliminar")

                .anyRequest().denyAll()
            )
            .httpBasic(basic -> basic.realmName("DondeJuana Hotel API"))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"No autorizado\"}");
            }));

        applySecureHeaders(http);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider
    ) throws Exception {

        http
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/").hasAuthority("dashboard.ver")
                .requestMatchers(HttpMethod.GET, "/calendario")
                    .hasAnyAuthority("calendario.ver", "ROLE_ADMINISTRADOR", "ROLE_RECEPCIONISTA")
                .requestMatchers(HttpMethod.GET, "/panel-administrativo")
                    .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_RECEPCIONISTA")

                .requestMatchers(HttpMethod.GET, "/personas").hasAuthority("persona.ver")
                .requestMatchers(HttpMethod.GET, "/personas/nuevo").hasAuthority("persona.crear")
                .requestMatchers(HttpMethod.GET, "/personas/editar/**").hasAuthority("persona.editar")
                .requestMatchers(HttpMethod.POST, "/personas/guardar").hasAnyAuthority("persona.crear", "persona.editar")
                .requestMatchers(HttpMethod.POST, "/personas/eliminar/**").hasAuthority("persona.eliminar")

                .requestMatchers(HttpMethod.GET, "/tipos-habitacion").hasAuthority("tipohabitacion.ver")
                .requestMatchers(HttpMethod.GET, "/tipos-habitacion/nuevo").hasAuthority("tipohabitacion.crear")
                .requestMatchers(HttpMethod.GET, "/tipos-habitacion/editar/**").hasAuthority("tipohabitacion.editar")
                .requestMatchers(HttpMethod.POST, "/tipos-habitacion/guardar").hasAnyAuthority("tipohabitacion.crear", "tipohabitacion.editar")
                .requestMatchers(HttpMethod.POST, "/tipos-habitacion/eliminar/**").hasAuthority("tipohabitacion.eliminar")

                .requestMatchers(HttpMethod.GET, "/habitaciones").hasAuthority("habitacion.ver")
                .requestMatchers(HttpMethod.GET, "/habitaciones/nuevo").hasAuthority("habitacion.crear")
                .requestMatchers(HttpMethod.GET, "/habitaciones/editar/**").hasAuthority("habitacion.editar")
                .requestMatchers(HttpMethod.POST, "/habitaciones/guardar").hasAnyAuthority("habitacion.crear", "habitacion.editar")
                .requestMatchers(HttpMethod.POST, "/habitaciones/*/estado").hasAuthority("habitacion.estado.cambiar")
                .requestMatchers(HttpMethod.POST, "/habitaciones/eliminar/**").hasAuthority("habitacion.eliminar")

                .requestMatchers(HttpMethod.GET, "/reservas").hasAuthority("reserva.ver")
                .requestMatchers(HttpMethod.GET, "/reservas/nuevo").hasAuthority("reserva.crear")
                .requestMatchers(HttpMethod.GET, "/reservas/editar/**").hasAuthority("reserva.editar")
                .requestMatchers(HttpMethod.POST, "/reservas/guardar").hasAnyAuthority("reserva.crear", "reserva.editar")
                .requestMatchers(HttpMethod.POST, "/reservas/eliminar/**").hasAuthority("reserva.eliminar")

                .anyRequest().denyAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(sm -> sm
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation(sf -> sf.migrateSession())
                .invalidSessionUrl("/login?expired=true")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        applySecureHeaders(http);
        return http.build();
    }

    private void applySecureHeaders(HttpSecurity http) throws Exception {
        http.headers(headers -> {
            headers.contentSecurityPolicy(csp -> csp.policyDirectives(
                    "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data:; object-src 'none'; frame-ancestors 'none'; base-uri 'self'"
            ));
            headers.referrerPolicy(ref -> ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER));
            headers.frameOptions(frame -> frame.deny());
            headers.permissionsPolicy(policy -> policy.policy("camera=(), microphone=(), geolocation=()"));
            headers.httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
            );
        });
    }
}
