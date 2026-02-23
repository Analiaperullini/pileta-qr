package com.pileta.pileta_qr.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "carnets")
public class Carnet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="socio_id", nullable = false)
    private Long socioId;

    @Column(name="membresia_id", nullable = false)
    private Long membresiaId;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Column(name="emitido_en")
    private LocalDateTime emitidoEn;

    @Column(nullable = false)
    private Boolean anulado = false;

    @Column(name="anulado_en")
    private LocalDateTime anuladoEn;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    @Column(name="fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    public Carnet() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSocioId() { return socioId; }
    public void setSocioId(Long socioId) { this.socioId = socioId; }

    public Long getMembresiaId() { return membresiaId; }
    public void setMembresiaId(Long membresiaId) { this.membresiaId = membresiaId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getEmitidoEn() { return emitidoEn; }
    public void setEmitidoEn(LocalDateTime emitidoEn) { this.emitidoEn = emitidoEn; }

    public Boolean getAnulado() { return anulado; }
    public void setAnulado(Boolean anulado) { this.anulado = anulado; }

    public LocalDateTime getAnuladoEn() { return anuladoEn; }
    public void setAnuladoEn(LocalDateTime anuladoEn) { this.anuladoEn = anuladoEn; }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}