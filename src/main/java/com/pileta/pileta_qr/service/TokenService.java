package com.pileta.pileta_qr.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.pileta.pileta_qr.repo.CarnetRepo;

@Service
public class TokenService {

    private final CarnetRepo carnetRepo;

    public TokenService(CarnetRepo carnetRepo) {
        this.carnetRepo = carnetRepo;
    }

    public String generarToken() {
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (carnetRepo.existsByToken(token));
        return token;
    }
}