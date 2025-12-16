package cl.duoc.ejemplo.microservicio.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import cl.duoc.ejemplo.microservicio.dto.ActualizarEventoRequest;
import cl.duoc.ejemplo.microservicio.dto.CompraResponse;
import cl.duoc.ejemplo.microservicio.dto.NuevaCompraRequest;
import cl.duoc.ejemplo.microservicio.dto.NuevoEventoRequest;
import cl.duoc.ejemplo.microservicio.model.Evento;
import cl.duoc.ejemplo.microservicio.service.TiendaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventosController {

    private final TiendaService service;

    public EventosController(TiendaService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar eventos",
        description = "Obtiene la lista de eventos disponibles con enlaces HATEOAS"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Eventos listados correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Evento>>> listar() {

        List<EntityModel<Evento>> eventos = service.listarEventos()
                .stream()
                .map(e -> EntityModel.of(e,
                        linkTo(methodOn(EventosController.class).listar()).withRel("eventos"),
                        linkTo(methodOn(EventosController.class).actualizar(e.getId(), null)).withRel("actualizar"),
                        linkTo(methodOn(EventosController.class).eliminar(e.getId())).withRel("eliminar")))
                .toList();

        return ResponseEntity.ok(
                CollectionModel.of(eventos,
                        linkTo(methodOn(EventosController.class).listar()).withSelfRel()));
    }

    @Operation(
        summary = "Crear evento",
        description = "Permite crear un nuevo evento y retorna enlaces de navegación"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evento creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Evento>> crear(@Valid @RequestBody NuevoEventoRequest dto) {

        Evento evento = service.crearEvento(dto);

        return ResponseEntity.ok(
                EntityModel.of(evento,
                        linkTo(methodOn(EventosController.class).listar()).withRel("eventos"),
                        linkTo(methodOn(EventosController.class).actualizar(evento.getId(), null))
                                .withRel("actualizar"),
                        linkTo(methodOn(EventosController.class).eliminar(evento.getId())).withRel("eliminar")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarEventoRequest dto) {
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
