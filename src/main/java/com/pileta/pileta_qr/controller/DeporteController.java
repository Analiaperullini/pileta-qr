package com.pileta.pileta_qr.controller;

import com.pileta.pileta_qr.model.Deporte;
import com.pileta.pileta_qr.repo.DeporteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deportes")
@CrossOrigin(origins = "*")
public class DeporteController {

    @Autowired
    private DeporteRepo deporteRepo;

    @GetMapping
    public List<Deporte> listarTodos() {
        return deporteRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Map<String, String> body) {
        // 1. Extraemos los datos del JSON que envía el HTML
        String nombre = body.get("nombre"); 
        String categoria = body.get("categoria");

        if (nombre == null || nombre.isBlank()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio");
        }

        // 2. Usamos el objeto Deporte y el repositorio (En lugar de SQL manual)
        Deporte nuevoDeporte = new Deporte();
        nuevoDeporte.setNombre(nombre);
        nuevoDeporte.setCategoria(categoria);

        deporteRepo.save(nuevoDeporte); // Esto asegura que JPA guarde los campos correctamente
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        deporteRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}