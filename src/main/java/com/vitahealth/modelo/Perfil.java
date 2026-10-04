package com.vitahealth.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

/**
 * Datos físicos y objetivo del usuario. Corresponde a la tabla "perfiles" (RF03, RF08).
 */
public class Perfil {

    private int idPerfil;
    private int idUsuario;
    private LocalDate fechaNacimiento;
    private BigDecimal pesoKg;
    private BigDecimal alturaCm;
    private String objetivo;

    public int getIdPerfil() { return idPerfil; }
    public void setIdPerfil(int idPerfil) { this.idPerfil = idPerfil; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }

    public BigDecimal getAlturaCm() { return alturaCm; }
    public void setAlturaCm(BigDecimal alturaCm) { this.alturaCm = alturaCm; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    /** Edad en años cumplidos, o null si no hay fecha de nacimiento. */
    public Integer getEdad() {
        if (fechaNacimiento == null) return null;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    /**
     * Índice de masa corporal = peso (kg) / altura (m)^2, redondeado a 1 decimal.
     * Devuelve null si falta el peso o la altura.
     */
    public BigDecimal getImc() {
        return calcularImc(pesoKg, alturaCm);
    }

    public static BigDecimal calcularImc(BigDecimal pesoKg, BigDecimal alturaCm) {
        if (pesoKg == null || alturaCm == null || alturaCm.signum() <= 0) return null;
        BigDecimal metros = alturaCm.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return pesoKg.divide(metros.multiply(metros), 1, RoundingMode.HALF_UP);
    }

    /** Clasificación del IMC según la OMS. */
    public String getClasificacionImc() {
        BigDecimal imc = getImc();
        if (imc == null) return "Sin datos";
        double v = imc.doubleValue();
        if (v < 18.5) return "Bajo peso";
        if (v < 25)   return "Peso saludable";
        if (v < 30)   return "Sobrepeso";
        return "Obesidad";
    }
}
