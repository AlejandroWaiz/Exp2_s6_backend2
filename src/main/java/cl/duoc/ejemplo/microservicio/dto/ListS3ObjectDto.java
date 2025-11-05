package cl.duoc.ejemplo.microservicio.dto;

import java.time.Instant;

public record ListS3ObjectDto(String key, long size, Instant lastModified) {}