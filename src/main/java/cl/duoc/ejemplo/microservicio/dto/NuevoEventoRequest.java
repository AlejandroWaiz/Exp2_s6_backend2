package cl.duoc.ejemplo.microservicio.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class NuevoEventoRequest {
    @NotBlank
    private String nombre;
    @NotNull
    private LocalDate fecha;
    @NotNull @DecimalMin("0.0")
    private BigDecimal precio;

    public String getNombre() { return nombre; }
    public LocalDate getFecha() { return fecha; }
    public BigDecimal getPrecio() { return precio; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
}
