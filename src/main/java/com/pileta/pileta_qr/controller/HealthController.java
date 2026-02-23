package com.pileta.pileta_qr.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

// @RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/health/db")
    public String healthDb() throws Exception {
        try (Connection c = dataSource.getConnection()) {
            return "DB OK -> " + c.getMetaData().getURL();
        }
    }
}
