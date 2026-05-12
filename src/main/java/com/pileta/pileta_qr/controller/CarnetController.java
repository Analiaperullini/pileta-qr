package com.pileta.pileta_qr.controller;

import com.google.zxing.WriterException;
import com.pileta.pileta_qr.dto.CarnetCreateRequest;
import com.pileta.pileta_qr.dto.CarnetCreateResponse;
import com.pileta.pileta_qr.dto.ValidacionResponse;
import com.pileta.pileta_qr.model.Carnet;
import com.pileta.pileta_qr.repo.CarnetRepo;
import com.pileta.pileta_qr.service.TokenService;
import com.pileta.pileta_qr.util.QrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.Optional;

@RestController
public class CarnetController {

    private final CarnetRepo carnetRepo;
    private final JdbcTemplate jdbcTemplate;
    private final TokenService tokenService;

    public CarnetController(CarnetRepo carnetRepo, JdbcTemplate jdbcTemplate, TokenService tokenService) {
        this.carnetRepo = carnetRepo;
        this.jdbcTemplate = jdbcTemplate;
        this.tokenService = tokenService;
    }

    private String baseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getHeader("Host");
    }

    @PostMapping("/api/carnets")
    public CarnetCreateResponse crearCarnet(@RequestBody CarnetCreateRequest req) {
        int mes = LocalDate.now().getMonthValue();
        int anio = LocalDate.now().getYear();
        LocalDate vence = YearMonth.of(anio, mes).atEndOfMonth();

        Optional<Carnet> existente = carnetRepo.findFirstBySocioIdAndMesAndAnioAndAnuladoFalse(req.socioId, mes, anio);

        if (existente.isPresent()) {
            Carnet c = existente.get();
            return new CarnetCreateResponse(c.getId(), c.getToken(), "/qr.png?token=" + c.getToken(), c.getMes(), c.getAnio(), c.getFechaVencimiento().toString());
        }

        Carnet c = new Carnet();
        c.setSocioId(req.socioId);
        c.setMembresiaId(req.membresiaId);
        c.setMes(mes);
        c.setAnio(anio);
        c.setFechaVencimiento(vence);
        c.setEmitidoEn(LocalDateTime.now());
        c.setAnulado(false);
        c.setToken(tokenService.generarToken());

        Carnet guardado = carnetRepo.save(c);
        return new CarnetCreateResponse(guardado.getId(), guardado.getToken(), "/qr.png?token=" + guardado.getToken(), guardado.getMes(), guardado.getAnio(), guardado.getFechaVencimiento().toString());
    }

    @GetMapping("/carnet/dni/{dni}")
    public ResponseEntity<Void> abrirCarnetPorDni(@PathVariable String dni, HttpServletRequest request) {
        try {
            Long socioId = jdbcTemplate.queryForObject("SELECT id FROM socios WHERE dni = ? LIMIT 1", Long.class, dni);
            String destino = baseUrl(request) + "/carnet/socio/" + socioId;
            return ResponseEntity.status(303).location(URI.create(destino)).build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/qr.png")
    public ResponseEntity<byte[]> qrPng(@RequestParam String token, HttpServletRequest request) throws Exception {
        BufferedImage img;
        try {
            String link = baseUrl(request) + "/c/" + token;
            img = QrUtil.toQrImage(link, 500);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return ResponseEntity.ok().headers(headers).cacheControl(CacheControl.noCache()).body(baos.toByteArray());
        } catch (WriterException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/c/{token}")
    public ResponseEntity<Void> redirigirCarnet(@PathVariable String token) {
        return ResponseEntity.status(302)
            .location(URI.create("/scan.html?token=" + token))
            .build();
    }

    @GetMapping("/validar-qr")
    public ValidacionResponse validar(@RequestParam String token) {
        Optional<Carnet> opt;
        if (token.matches("[0-9a-fA-F]{8}")) {
            opt = carnetRepo.findFirstByTokenStartingWith(token);
        } else {
            opt = carnetRepo.findFirstByToken(token);
        }
        if (opt.isEmpty()) return new ValidacionResponse("TOKEN_INVALIDO", null, null, "Token no existe");

        Carnet c = opt.get();
        String nombre;
        try {
            nombre = jdbcTemplate.queryForObject("SELECT nombre FROM socios WHERE id = ?", String.class, c.getSocioId());
        } catch (Exception e) { nombre = "Socio #" + c.getSocioId(); }

        if (Boolean.TRUE.equals(c.getAnulado())) {
            return new ValidacionResponse("ANULADO", nombre, c.getFechaVencimiento(), "Carnet anulado");
        }

        if (c.getFechaVencimiento() != null && c.getFechaVencimiento().isBefore(LocalDate.now())) {
            return new ValidacionResponse("VENCIDO", nombre, c.getFechaVencimiento(),
                "Vencido el " + c.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        return new ValidacionResponse("OK", nombre, c.getFechaVencimiento(), "Habilitado");
    }
}