package com.pileta.pileta_qr.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "socios")
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dni;
    private String nombre;
    private String telefono;
    private String email;
    private String fotoPath;

    @ManyToMany
    @JoinTable(
      name = "socio_deportes",
      joinColumns = @JoinColumn(name = "socio_id"),
      inverseJoinColumns = @JoinColumn(name = "deporte_id")
    )
    private List<Deporte> deportes = new ArrayList<>();

    // --- CONSTRUCTORES ---
    public Socio() {}

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFotoPath() { return fotoPath; }
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }
    public List<Deporte> getDeportes() { return deportes; }
    public void setDeportes(List<Deporte> deportes) { this.deportes = deportes; }
}