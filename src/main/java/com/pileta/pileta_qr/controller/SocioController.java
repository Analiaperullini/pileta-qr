package com.pileta.pileta_qr.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller; // Importante: @Controller, no @RestController
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model; // CORRECCIÓN: Importación del Model de Spring

import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
public class SocioController {

    private final JdbcTemplate jdbcTemplate;

    public SocioController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * RENDERIZA EL CARNET (HTML con Thymeleaf)
     */
    @GetMapping("/carnet/socio/{id}")
    public String verCarnet(@PathVariable Long id, Model model) {
        try {
            // Buscamos datos del socio
            Map<String, Object> socio = jdbcTemplate.queryForList(
                "SELECT * FROM socios WHERE id = ?", id).get(0);
            
            // Buscamos deportes asociados
            List<Map<String, Object>> deportes = jdbcTemplate.queryForList(
                "SELECT d.nombre FROM deportes d " +
                "JOIN socio_deportes sd ON d.id = sd.deporte_id " +
                "WHERE sd.socio_id = ?", id);
            
            model.addAttribute("socio", socio);
            model.addAttribute("deportes", deportes);
            
            // Busca carnet.html en src/main/resources/templates/
            return "carnet"; 
            
        } catch (Exception e) {
            return "error"; 
        }
    }

    /**
     * API: CREAR SOCIO (Retorna JSON)
     */
    @PostMapping(value = "/api/socios", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody // Indica que este método devuelve JSON y no una vista HTML
    public ResponseEntity<?> crearSocio(
            @RequestParam String nombre,
            @RequestParam String dni,
            @RequestParam(required = false, defaultValue = "") String telefono,
            @RequestParam(required = false, defaultValue = "") String email,
            @RequestParam(required = false) String deportes,
            @RequestParam(required = false) MultipartFile foto
    ) throws Exception {
        // ... (Lógica de guardado de imagen y base de datos) ...
        // Este código se mantiene igual al que tenías originalmente
        // Simulación de respuesta para mantener el ejemplo conciso
        return ResponseEntity.ok(Map.of("ok", true, "msg", "Socio creado"));
    }

    /**
     * API: BUSCAR/FILTRAR (Retorna JSON)
     */
    @GetMapping("/api/socios/buscar")
    @ResponseBody // Necesario al usar @Controller
    public ResponseEntity<?> buscar(@RequestParam(required = false, defaultValue = "") String q) {
        String sql = "SELECT * FROM socios WHERE nombre LIKE ? OR dni LIKE ?";
        var lista = jdbcTemplate.queryForList(sql, "%" + q + "%", "%" + q + "%");
        return ResponseEntity.ok(lista);
    }
}