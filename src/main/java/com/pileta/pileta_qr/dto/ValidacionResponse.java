package com.pileta.pileta_qr.dto;

import java.time.LocalDate;

public class ValidacionResponse {
  public String resultado;
  public String nombre;
  public LocalDate vencimiento;
  public String detalle;

  public ValidacionResponse(String resultado, String nombre, LocalDate vencimiento, String detalle) {
    this.resultado = resultado;
    this.nombre = nombre;
    this.vencimiento = vencimiento;
    this.detalle = detalle;
  }
}