package com.pileta.pileta_qr.controller;

import com.pileta.pileta_qr.dto.ValidacionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;

// @RestController
public class ValidacionController {

    private final DataSource dataSource;

    public ValidacionController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/validar-qr")
    public ValidacionResponse validar(@RequestParam String token) throws Exception {

        String sql = """
            SELECT
              c.id AS carnet_id,
              c.socio_id AS socio_id,
              c.anulado AS anulado,
              m.estado AS estado_membresia,
              m.fecha_vencimiento AS fecha_vencimiento,
              s.nombre AS nombre
            FROM carnets c
            JOIN membresias m ON m.id = c.membresia_id
            JOIN socios s ON s.id = c.socio_id
            WHERE c.token = ?
            LIMIT 1
            """;

        try (Connection conn = dataSource.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, token);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        insertarIngreso(conn, null, null, null, "TOKEN_INVALIDO", "Token no existe: " + token);
                        return new ValidacionResponse("TOKEN_INVALIDO", null, null, "Token no existe");
                    }

                    long carnetId = rs.getLong("carnet_id");
                    long socioId = rs.getLong("socio_id");
                    boolean anulado = rs.getBoolean("anulado");
                    String estadoMembresia = rs.getString("estado_membresia");
                    Date vencSql = rs.getDate("fecha_vencimiento");
                    LocalDate vencimiento = vencSql != null ? vencSql.toLocalDate() : null;
                    String nombre = rs.getString("nombre");

                    String resultado;
                    String detalle;

                    if (anulado) {
                        resultado = "ANULADO";
                        detalle = "Carnet anulado";
                    } else if ("SUSPENDIDA".equalsIgnoreCase(estadoMembresia)) {
                        resultado = "SUSPENDIDO";
                        detalle = "Membresía suspendida";
                    } else if (vencimiento == null) {
                        resultado = "SIN_MEMBRESIA";
                        detalle = "Sin vencimiento (datos incompletos)";
                    } else if (vencimiento.isBefore(LocalDate.now())) {
                        resultado = "VENCIDO";
                        detalle = "Vencida el " + vencimiento;
                    } else {
                        resultado = "OK";
                        detalle = "Habilitado hasta " + vencimiento;
                    }

                    insertarIngreso(conn, carnetId, socioId, null, resultado, detalle);

                    return new ValidacionResponse(resultado, nombre, vencimiento, detalle);
                }
            }
        }
    }

    private void insertarIngreso(Connection conn, Long carnetId, Long socioId, Long usuarioId,
                                 String resultado, String detalle) throws SQLException {
        String ins = """
            INSERT INTO ingresos (carnet_id, socio_id, usuario_id, resultado, detalle)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(ins)) {
            if (carnetId == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, carnetId);
            if (socioId == null) ps.setNull(2, Types.BIGINT); else ps.setLong(2, socioId);
            if (usuarioId == null) ps.setNull(3, Types.BIGINT); else ps.setLong(3, usuarioId);
            ps.setString(4, resultado);
            ps.setString(5, detalle);
            ps.executeUpdate();
        }
    }
}
