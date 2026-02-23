package com.pileta.pileta_qr.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Service;
import com.pileta.pileta_qr.repo.CarnetRepo;

@Service
public class TokenService {

    private final CarnetRepo carnetRepo;
    private final SecureRandom rnd = new SecureRandom();

    public TokenService(CarnetRepo carnetRepo) {
        this.carnetRepo = carnetRepo;
    }

    // 6 dígitos (000000-999999)
    public String generarToken6() {
        String token;
        do {
            int n = rnd.nextInt(1_000_000);
            token = String.format("%06d", n);
        } while (carnetRepo.existsByToken(token));
        return token;
    }
}