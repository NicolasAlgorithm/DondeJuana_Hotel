package com.project.hotel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotel.entities.Habitacion;
import com.project.hotel.entities.Persona;
import com.project.hotel.entities.TipoHabitacion;
import com.project.hotel.repository.HabitacionRepository;
import com.project.hotel.repository.PersonaRepository;
import com.project.hotel.repository.ReservaRepository;
import com.project.hotel.repository.TipoHabitacionRepository;
import com.project.hotel.service.DbUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockHttpSession;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class E2EFlujosIntegracionTest {

    private static final String USERNAME = "e2e-user";
    private static final String PASSWORD = "e2e-pass";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @MockBean
    private DbUserDetailsService dbUserDetailsService;

    @BeforeEach
    void setUp() {
        reservaRepository.deleteAll();
        habitacionRepository.deleteAll();
        tipoHabitacionRepository.deleteAll();
        personaRepository.deleteAll();

        String encodedPassword = new BCryptPasswordEncoder().encode(PASSWORD);
        Mockito.when(dbUserDetailsService.loadUserByUsername(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String username = invocation.getArgument(0, String.class);
                    if (!USERNAME.equals(username)) {
                        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
                    }
                    return User.withUsername(USERNAME)
                            .password(encodedPassword)
                            .authorities(
                                    "dashboard.ver",
                                    "reserva.ver",
                                    "reserva.crear",
                                    "reserva.checkin",
                                    "reserva.checkout",
                                    "calendario.ver"
                            )
                            .build();
                });
    }

    @Test
    void login_debeCubrirExitoErrorYSesion() throws Exception {
        MvcResult loginExitoso = mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin("/login")
                        .user(USERNAME)
                        .password(PASSWORD))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin("/login")
                        .user("usuario-invalido")
                        .password("clave-invalida"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error=true"));

        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin("/login")
                        .user("usuario-invalido")
                        .password("clave-invalida"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error=true"));

        MockHttpSession session = (MockHttpSession) loginExitoso.getRequest().getSession(false);
        assertNotNull(session);

        mockMvc.perform(get("/reservas").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(post("/logout")
                        .session(session)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?logout=true"));

        mockMvc.perform(get("/reservas").session(session))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void reserva_debeCrearYConsultarResultadoFinal() throws Exception {
        Persona persona = crearPersona();
        Habitacion habitacion = crearHabitacion("HAB-E2E-R1", "101", BigDecimal.valueOf(120));

        Long idReserva = crearReserva(persona.getIdPersona(), habitacion.getIdHabitacion(),
                LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 13));

        MvcResult consulta = mockMvc.perform(get("/api/reservas/" + idReserva)
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(consulta.getResponse().getContentAsString());
        assertEquals(idReserva.longValue(), body.get("idReserva").asLong());
        assertEquals(persona.getIdPersona().longValue(), body.get("idPersona").asLong());
        assertEquals(habitacion.getIdHabitacion().longValue(), body.get("idHabitacion").asLong());
        assertEquals("ACTIVA", body.get("estado").asText());
    }

    @Test
    void checkInYCheckOut_debeCompletarTransicionDeEstados() throws Exception {
        Persona persona = crearPersona();
        Habitacion habitacion = crearHabitacion("HAB-E2E-R2", "102", BigDecimal.valueOf(150));

        Long idReserva = crearReserva(persona.getIdPersona(), habitacion.getIdHabitacion(),
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 4));

        MvcResult checkIn = mockMvc.perform(patch("/api/reservas/" + idReserva + "/checkin")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode checkInBody = objectMapper.readTree(checkIn.getResponse().getContentAsString());
        assertEquals("EN_ESTADIA", checkInBody.get("estado").asText());
        assertTrue(checkInBody.hasNonNull("fechaHoraCheckIn"));

        MvcResult checkOut = mockMvc.perform(patch("/api/reservas/" + idReserva + "/checkout")
                        .param("fechaSalidaReal", "2026-06-04")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode checkOutBody = objectMapper.readTree(checkOut.getResponse().getContentAsString());
        assertEquals("CUMPLIDA", checkOutBody.get("estado").asText());
        assertEquals("2026-06-04", checkOutBody.get("fechaSalidaReal").asText());
    }

    @Test
    void pago_debeConfirmarMontoTotalTrasFlujoCompleto() throws Exception {
        Persona persona = crearPersona();
        Habitacion habitacion = crearHabitacion("HAB-E2E-R3", "103", BigDecimal.valueOf(200));

        Long idReserva = crearReserva(persona.getIdPersona(), habitacion.getIdHabitacion(),
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 4));

        mockMvc.perform(patch("/api/reservas/" + idReserva + "/checkin")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/reservas/" + idReserva + "/checkout")
                        .param("fechaSalidaReal", "2026-07-04")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk());

        MvcResult listado = mockMvc.perform(get("/api/reservas")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode reservas = objectMapper.readTree(listado.getResponse().getContentAsString());
        JsonNode reserva = buscarReservaPorId(reservas, idReserva);
        assertNotNull(reserva, "Debe existir la reserva creada en el listado");
        assertEquals("CUMPLIDA", reserva.get("estado").asText());
        assertEquals(600.0, reserva.get("total").asDouble(), 0.001);
    }

    @Test
    void reportes_debeRetornarOcupacionEsperadaEnCalendario() throws Exception {
        Persona persona = crearPersona();
        Habitacion habitacion = crearHabitacion("HAB-E2E-R4", "104", BigDecimal.valueOf(175));

        Long idReserva = crearReserva(persona.getIdPersona(), habitacion.getIdHabitacion(),
                LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 13));

        MvcResult reporte = mockMvc.perform(get("/api/calendario")
                        .param("fechaInicio", "2026-08-10")
                        .param("fechaFin", "2026-08-14")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(reporte.getResponse().getContentAsString());
        assertEquals(1, body.get("totalHabitaciones").asInt());

        JsonNode dias = body.get("habitaciones").get(0).get("dias");
        boolean hayDiaOcupado = false;
        for (JsonNode dia : dias) {
            if ("OCUPADA".equals(dia.get("estado").asText()) && dia.get("idReserva").asLong() == idReserva) {
                hayDiaOcupado = true;
                break;
            }
        }
        assertTrue(hayDiaOcupado, "El reporte debe reflejar la reserva activa en al menos un día");
    }

    private Persona crearPersona() {
        Persona persona = new Persona("Ana", "Pérez", "CC", "12345678", "ana@hotel.test", "3001234567");
        return personaRepository.save(persona);
    }

    private Habitacion crearHabitacion(String codigo, String numero, BigDecimal tarifaNoche) {
        TipoHabitacion tipo = new TipoHabitacion("Suite " + numero, "Habitación E2E", tarifaNoche);
        TipoHabitacion tipoGuardado = tipoHabitacionRepository.save(tipo);

        Habitacion habitacion = new Habitacion();
        habitacion.setCodigo(codigo);
        habitacion.setNumero(numero);
        habitacion.setPiso(1);
        habitacion.setTipoHabitacion(tipoGuardado);
        habitacion.setTarifaNoche(tarifaNoche);
        habitacion.setEstado("DISPONIBLE");
        habitacion.setActivo("S");
        return habitacionRepository.save(habitacion);
    }

    private Long crearReserva(Long idPersona, Long idHabitacion, LocalDate fechaEntrada, LocalDate fechaSalida) throws Exception {
        Map<String, Object> body = Map.of(
                "idPersona", idPersona,
                "idHabitacion", idHabitacion,
                "fechaEntrada", fechaEntrada.toString(),
                "fechaSalida", fechaSalida.toString(),
                "estado", "ACTIVA"
        );

        MvcResult response = mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode reserva = objectMapper.readTree(response.getResponse().getContentAsString());
        return reserva.get("idReserva").asLong();
    }

    private JsonNode buscarReservaPorId(JsonNode reservas, Long idReserva) {
        for (JsonNode reserva : reservas) {
            if (reserva.has("idReserva") && reserva.get("idReserva").asLong() == idReserva) {
                return reserva;
            }
        }
        return null;
    }
}
