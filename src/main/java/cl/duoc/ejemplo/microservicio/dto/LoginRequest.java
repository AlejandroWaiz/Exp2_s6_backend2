package cl.duoc.ejemplo.microservicio.dto;

public record LoginRequest(
    String username,
    String password
) {}
