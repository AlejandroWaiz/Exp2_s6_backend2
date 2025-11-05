package cl.duoc.ejemplo.microservicio.dto;

import jakarta.validation.constraints.*;

public class NuevaCompraRequest {
    @NotNull
    private Long eventoId;
    @NotNull @Min(1)
    private Integer cantidad;

    public Long getEventoId() { return eventoId; }
    public Integer getCantidad() { return cantidad; }

    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
