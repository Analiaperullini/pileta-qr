package com.pileta.pileta_qr.controller;

import com.pileta.pileta_qr.model.Carnet;
import com.pileta.pileta_qr.model.Deporte;
import com.pileta.pileta_qr.model.Socio;
import com.pileta.pileta_qr.repo.CarnetRepo;
import com.pileta.pileta_qr.repo.DeporteRepo;
import com.pileta.pileta_qr.repo.SocioRepo;
import com.pileta.pileta_qr.service.TokenService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class SocioController {

    private final JdbcTemplate jdbcTemplate;
    private final SocioRepo socioRepo;
    private final DeporteRepo deporteRepo;
    private final CarnetRepo carnetRepo;
    private final TokenService tokenService;

    public SocioController(JdbcTemplate jdbcTemplate, SocioRepo socioRepo, DeporteRepo deporteRepo,
                           CarnetRepo carnetRepo, TokenService tokenService) {
        this.jdbcTemplate = jdbcTemplate;
        this.socioRepo = socioRepo;
        this.deporteRepo = deporteRepo;
        this.carnetRepo = carnetRepo;
        this.tokenService = tokenService;
    }

    @GetMapping("/carnet/socio/{id}")
    public String verCarnet(@PathVariable Long id, Model model) {
        try {
            Map<String, Object> socio = jdbcTemplate.queryForList(
                "SELECT * FROM socios WHERE id = ?", id).get(0);

            List<Map<String, Object>> deportes = jdbcTemplate.queryForList(
                "SELECT d.nombre FROM deportes d " +
                "JOIN socio_deportes sd ON d.id = sd.deporte_id " +
                "WHERE sd.socio_id = ?", id);

            int mes = LocalDate.now().getMonthValue();
            int anio = LocalDate.now().getYear();
            Optional<Carnet> carnetOpt = carnetRepo.findFirstBySocioIdAndMesAndAnioAndAnuladoFalse(id, mes, anio);
            Carnet carnet;
            if (carnetOpt.isPresent()) {
                carnet = carnetOpt.get();
            } else {
                carnet = new Carnet();
                carnet.setSocioId(id);
                carnet.setMembresiaId(1L);
                carnet.setMes(mes);
                carnet.setAnio(anio);
                carnet.setFechaVencimiento(YearMonth.of(anio, mes).atEndOfMonth());
                carnet.setEmitidoEn(LocalDateTime.now());
                carnet.setAnulado(false);
                carnet.setToken(tokenService.generarToken());
                carnet = carnetRepo.save(carnet);
            }

            model.addAttribute("socio", socio);
            model.addAttribute("deportes", deportes);
            model.addAttribute("carnetToken", carnet.getToken());
            model.addAttribute("carnetVence", carnet.getFechaVencimiento());
            return "carnet";
        } catch (Exception e) {
            return "error";
        }
    }

    @PostMapping(value = "/api/socios", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> crearSocio(
            @RequestParam String nombre,
            @RequestParam String dni,
            @RequestParam(required = false, defaultValue = "") String telefono,
            @RequestParam(required = false, defaultValue = "") String email,
            @RequestParam(required = false, defaultValue = "") String deportes,
            @RequestParam(required = false) MultipartFile foto
    ) {
        try {
            Socio socio = new Socio();
            socio.setNombre(nombre);
            socio.setDni(dni);
            socio.setTelefono(telefono);
            socio.setEmail(email);

            if (foto != null && !foto.isEmpty()) {
                Path dir = Paths.get(System.getProperty("user.dir"), "uploads", "fotos");
                Files.createDirectories(dir);
                String filename = UUID.randomUUID() + "_" + foto.getOriginalFilename();
                Files.copy(foto.getInputStream(), dir.resolve(filename));
                socio.setFotoPath("/fotos/" + filename);
            }

            if (!deportes.isBlank()) {
                List<Deporte> lista = new ArrayList<>();
                for (String parte : deportes.split(",")) {
                    String idStr = parte.trim();
                    if (!idStr.isEmpty()) {
                        try {
                            Long depId = Long.parseLong(idStr);
                            deporteRepo.findById(depId).ifPresent(lista::add);
                        } catch (NumberFormatException ignored) {}
                    }
                }
                socio.setDeportes(lista);
            }

            Socio guardado = socioRepo.save(socio);
            return ResponseEntity.ok(Map.of("id", guardado.getId()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("msg", "Error al guardar el socio: " + e.getMessage()));
        }
    }

    @GetMapping("/api/socios/buscar")
    @ResponseBody
    public ResponseEntity<?> buscar(@RequestParam(required = false, defaultValue = "") String q) {
        String sql = "SELECT * FROM socios WHERE nombre LIKE ? OR dni LIKE ?";
        var lista = jdbcTemplate.queryForList(sql, "%" + q + "%", "%" + q + "%");
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/api/socios/filtrar")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<?> filtrar(
            @RequestParam(required = false, defaultValue = "") String nombre,
            @RequestParam(required = false, defaultValue = "") String dni,
            @RequestParam(required = false) String deporteId
    ) {
        Long depId = null;
        if (deporteId != null && !deporteId.isBlank()) {
            try { depId = Long.parseLong(deporteId); } catch (NumberFormatException ignored) {}
        }
        String n = nombre.isBlank() ? null : nombre;
        String d = dni.isBlank() ? null : dni;
        List<Socio> socios = socioRepo.filtrarSocios(n, d, depId);
        socios.forEach(s -> s.getDeportes().size()); // initialize lazy collection
        return ResponseEntity.ok(socios);
    }
}
