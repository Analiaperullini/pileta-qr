package com.pileta.pileta_qr.dto;

public class CarnetCreateResponse {
      public Long id;
    public String tokenGenerado;
    public String urlQr;
    public Integer mes;
    public Integer anio;
    public String vence;

    public CarnetCreateResponse(Long id, String tokenGenerado, String urlQr, Integer mes, Integer anio, String vence) {
        this.id = id;
        this.tokenGenerado = tokenGenerado;
        this.urlQr = urlQr;
        this.mes = mes;
        this.anio = anio;
        this.vence = vence;
    }
}
