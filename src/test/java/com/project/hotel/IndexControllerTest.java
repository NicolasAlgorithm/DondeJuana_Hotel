package com.project.hotel;

import com.project.hotel.controller.IndexController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class IndexControllerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private IndexController indexController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void index_muestraEstadoOk_siConsultaDbFunciona() {
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ADMIN.USUARIOS", Long.class)).thenReturn(3L);

        Model model = new ExtendedModelMap();
        String view = indexController.index(model);

        assertEquals("index", view);
        assertEquals("OK", model.getAttribute("dbStatus"));
        assertEquals("Conectado · 3 usuario(s) en BD", model.getAttribute("dbDetail"));
    }

    @Test
    void index_muestraEstadoError_siConsultaDbFalla() {
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ADMIN.USUARIOS", Long.class))
                .thenThrow(new RuntimeException("db down"));

        Model model = new ExtendedModelMap();
        String view = indexController.index(model);

        assertEquals("index", view);
        assertEquals("ERROR", model.getAttribute("dbStatus"));
        assertEquals("No se pudo conectar a la base de datos. Revisa la configuración.", model.getAttribute("dbDetail"));
    }
}
