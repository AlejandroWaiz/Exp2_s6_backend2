package cl.duoc.ejemplo.microservicio.dto;

import jakarta.validation.constraints.NotBlank;

public record NewTicketRequest(
    @NotBlank String eventoId,
    @NotBlank String usuarioId,
    @NotBlank String titulo,
    String contenidoHtml
) {}
