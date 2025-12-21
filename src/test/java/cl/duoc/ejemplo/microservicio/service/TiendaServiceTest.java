package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.dto.ActualizarEventoRequest;
import cl.duoc.ejemplo.microservicio.dto.NuevaCompraRequest;
import cl.duoc.ejemplo.microservicio.dto.NuevoEventoRequest;
import cl.duoc.ejemplo.microservicio.model.Evento;
import cl.duoc.ejemplo.microservicio.repo.CompraRepository;
import cl.duoc.ejemplo.microservicio.repo.EventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TiendaService.class) // <-- importa tu service real usando repos reales en H2
class TiendaServiceTest {

    @Autowired
    TiendaService service;

    @Autowired
    EventoRepository eventoRepo;

    @Autowired
    CompraRepository compraRepo;

    @Test
    void crearEvento_persisteEnBaseDeDatos() {
        NuevoEventoRequest dto = new NuevoEventoRequest();
        dto.setNombre("Evento Test");
        dto.setFecha(LocalDate.of(2026, 1, 10));
        dto.setPrecio(new BigDecimal("9990.00"));

        Evento created = service.crearEvento(dto);

        assertNotNull(created.getId());
        assertEquals("Evento Test", created.getNombre());
        assertTrue(eventoRepo.existsById(created.getId()));
    }

    @Test
    void comprar_calculaTotalCorrecto_y_persisteCompra() {
        // Arrange: crear evento real en H2
        Evento ev = new Evento("Vacunación", LocalDate.of(2026, 1, 10), new BigDecimal("10000.00"));
        ev = eventoRepo.save(ev);

        NuevaCompraRequest req = new NuevaCompraRequest();
        req.setEventoId(ev.getId());
        req.setCantidad(2);

        // Act
        var resp = service.comprar(req);

        // Assert
        assertNotNull(resp.getCompraId());
        assertEquals(ev.getId(), resp.getEventoId());
        assertEquals(2, resp.getCantidad());
        assertEquals(new BigDecimal("10000.00"), resp.getPrecioUnitario());
        assertEquals(new BigDecimal("20000.00"), resp.getPrecioTotal());
        assertEquals(1, compraRepo.count());
    }

    @Test
    void comprar_eventoNoExiste_lanzaIllegalArgumentException() {
        NuevaCompraRequest req = new NuevaCompraRequest();
        req.setEventoId(99999L);
        req.setCantidad(1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.comprar(req));
        assertTrue(ex.getMessage().toLowerCase().contains("evento no encontrado"));
    }

    @Test
    void actualizarEvento_actualizaCampos() {
        Evento ev = eventoRepo.save(new Evento("Viejo", LocalDate.of(2026, 1, 1), new BigDecimal("5000.00")));

        ActualizarEventoRequest dto = new ActualizarEventoRequest();
        dto.setNombre("Nuevo");
        dto.setFecha(LocalDate.of(2026, 2, 2));
        dto.setPrecio(new BigDecimal("7000.00"));

        Evento updated = service.actualizarEvento(ev.getId(), dto);

        assertEquals("Nuevo", updated.getNombre());
        assertEquals(LocalDate.of(2026, 2, 2), updated.getFecha());
        assertEquals(new BigDecimal("7000.00"), updated.getPrecio());
    }

    @Test
    void eliminarEvento_eventoNoExiste_lanzaIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.eliminarEvento(12345L));
        assertTrue(ex.getMessage().toLowerCase().contains("evento no encontrado"));
    }
}
