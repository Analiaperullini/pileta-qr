package com.pileta.pileta_qr.repo;

import com.pileta.pileta_qr.model.Carnet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarnetRepo extends JpaRepository<Carnet, Long> {

    Optional<Carnet> findFirstBySocioIdAndMesAndAnioAndAnuladoFalse(Long socioId, Integer mes, Integer anio);

    Optional<Carnet> findFirstByTokenAndAnuladoFalse(String token);

    boolean existsByToken(String token);
}