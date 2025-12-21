package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.NuevaCompraRequest;
import cl.duoc.ejemplo.microservicio.dto.NuevoEventoRequest;
import cl.duoc.ejemplo.microservicio.model.Evento;
import cl.duoc.ejemplo.microservicio.service.TiendaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventosController.class)
@AutoConfigureMockMvc(addFilters = false) // <-- evitamos Security acá (unit test del controller)
class EventosControllerWebMvcTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    TiendaService service;

    @MockBean
    cl.duoc.ejemplo.microservicio.security.jwt.JwtService jwtService;

    @MockBean
    cl.duoc.ejemplo.microservicio.security.jwt.JwtAuthFilter jwtAuthFilter;

    @Test
    void listar_retorna200_y_contieneLinksHateoas() throws Exception {
        Evento e1 = new Evento("Evento 1", LocalDate.of(2026, 1, 10), new BigDecimal("1000.00"));
        e1.setId(1L);
        Evento e2 = new Evento("Evento 2", LocalDate.of(2026, 2, 10), new BigDecimal("2000.00"));
        e2.setId(2L);

        when(service.listarEventos()).thenReturn(List.of(e1, e2));

        mvc.perform(get("/eventos"))
                .andExpect(status().isOk())
                // self link del collection
                .andExpect(jsonPath("$._links.self.href").exists())
                // al menos un link "eventos" en elementos (depende del orden, pero existe)
                .andExpect(jsonPath("$._embedded").exists());

        verify(service).listarEventos();
    }

    @Test
    void crear_conBodyInvalido_retorna400() throws Exception {
        // nombre vacío + fecha null + precio negativo => @Valid debería tirar 400
        String body = """
                { "nombre": "", "fecha": null, "precio": -10 }
                """;

        mvc.perform(post("/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());

        verify(service, never()).crearEvento(any(NuevoEventoRequest.class));
    }

    @Test
    void comprar_cantidadCero_retorna400_porValidacion() throws Exception {
        // cantidad @Min(1)
        NuevaCompraRequest req = new NuevaCompraRequest();
        req.setEventoId(1L);
        req.setCantidad(0);

        mvc.perform(post("/eventos/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(service, never()).comprar(any(NuevaCompraRequest.class));
    }
}
