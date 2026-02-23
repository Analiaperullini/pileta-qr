package com.pileta.pileta_qr.repo;

import com.pileta.pileta_qr.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SocioRepo extends JpaRepository<Socio, Long> {

    @Query("SELECT DISTINCT s FROM Socio s LEFT JOIN s.deportes d WHERE " +
           "(:nombre IS NULL OR lower(s.nombre) LIKE lower(concat('%', :nombre, '%'))) AND " +
           "(:dni IS NULL OR s.dni LIKE concat('%', :dni, '%')) AND " +
           "(:deporteId IS NULL OR d.id = :deporteId)")
    List<Socio> filtrarSocios(@Param("nombre") String nombre, 
                             @Param("dni") String dni, 
                             @Param("deporteId") Long deporteId);
}