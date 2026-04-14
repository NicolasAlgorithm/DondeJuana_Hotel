package com.project.hotel;

import com.project.hotel.dto.PanelReporteDTO;
import com.project.hotel.controller.PanelAdministrativoController;
import com.project.hotel.service.PanelAdministrativoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class PanelAdministrativoControllerTest {

    @Mock
    private PanelAdministrativoService panelAdministrativoService;

    private PanelAdministrativoController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new PanelAdministrativoController(panelAdministrativoService);
    }

    @Test
    void verPanel_muestraSoloDiario_paraRecepcionista() {
        LocalDate fecha = LocalDate.of(2026, 4, 10);
        when(panelAdministrativoService.reporteDiario(fecha)).thenReturn(dummy("Diario"));

        Model model = new ConcurrentModel();
        var auth = new UsernamePasswordAuthenticationToken(
                "recepcion", "N/A", List.of(new SimpleGrantedAuthority("ROLE_RECEPCIONISTA")));

        String vista = controller.verPanel(fecha, auth, model);

        assertEquals("panel-administrativo/ocupacion-ingresos", vista);
        assertFalse((Boolean) model.getAttribute("esAdmin"));
        assertNotNull(model.getAttribute("reporteDiario"));
        assertNull(model.getAttribute("reporteSemanal"));
        assertNull(model.getAttribute("reporteMensual"));
    }

    @Test
    void verPanel_muestraDiarioSemanalMensual_paraAdministrador() {
        LocalDate fecha = LocalDate.of(2026, 4, 10);
        when(panelAdministrativoService.reporteDiario(fecha)).thenReturn(dummy("Diario"));
        when(panelAdministrativoService.reporteSemanal(fecha)).thenReturn(dummy("Semanal"));
        when(panelAdministrativoService.reporteMensual(fecha)).thenReturn(dummy("Mensual"));

        Model model = new ConcurrentModel();
        var auth = new UsernamePasswordAuthenticationToken(
                "admin", "N/A", List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));

        controller.verPanel(fecha, auth, model);

        assertTrue((Boolean) model.getAttribute("esAdmin"));
        assertNotNull(model.getAttribute("reporteDiario"));
        assertNotNull(model.getAttribute("reporteSemanal"));
        assertNotNull(model.getAttribute("reporteMensual"));
    }

    private PanelReporteDTO dummy(String periodo) {
        return new PanelReporteDTO(
                periodo,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 1),
                0,
                0,
                0,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0
        );
    }
}
