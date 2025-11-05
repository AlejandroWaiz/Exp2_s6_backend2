package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.ActualizarEventoRequest;
import cl.duoc.ejemplo.microservicio.dto.CompraResponse;
import cl.duoc.ejemplo.microservicio.dto.NuevaCompraRequest;
import cl.duoc.ejemplo.microservicio.dto.NuevoEventoRequest;
import cl.duoc.ejemplo.microservicio.model.Evento;
import cl.duoc.ejemplo.microservicio.service.TiendaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventosController {

    private final TiendaService service;

    public EventosController(TiendaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listar() {
        return ResponseEntity.ok(service.listarEventos());
    }

    @PostMapping
    public ResponseEntity<Evento> crear(@Valid @RequestBody NuevoEventoRequest dto) {
        return ResponseEntity.ok(service.crearEvento(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizar(@PathVariable Long id,@Valid @RequestBody ActualizarEventoRequest dto) {
        return ResponseEntity.ok(service.actualizarEvento(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/compras")
    public ResponseEntity<CompraResponse> comprar(@Valid @RequestBody NuevaCompraRequest dto) {
        return ResponseEntity.ok(service.comprar(dto));
    }
}
