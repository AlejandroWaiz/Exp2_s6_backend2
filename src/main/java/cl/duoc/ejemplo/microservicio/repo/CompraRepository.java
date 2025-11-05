package cl.duoc.ejemplo.microservicio.repo;

import cl.duoc.ejemplo.microservicio.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraRepository extends JpaRepository<Compra, Long> {}
