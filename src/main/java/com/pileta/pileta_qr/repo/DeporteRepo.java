package com.pileta.pileta_qr.repo;

import com.pileta.pileta_qr.model.Deporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeporteRepo extends JpaRepository<Deporte, Long> {
    // Esto ya incluye automáticamente: guardar, eliminar, buscar por ID y listar todos.
}