package com.vitahealth.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Actividad física realizada por un usuario. Corresponde a la tabla "registro_actividad" (RF05).
 */
public class RegistroActividad {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("EEE d 'de' MMM", Locale.forLanguageTag("es-CO"));

    private int idRegistro;
    private int idUsuario;
    private int idActividad;
    private String nombreActividad;   // dato de la tabla actividades (JOIN)
    private String tipoActividad;     // dato de la tabla actividades (JOIN)
    private LocalDate fecha;
    private int duracionMinutos;
    private BigDecimal caloriasQuemadas;

    /**
     * Calorías quemadas = calorías por hora de la actividad * (minutos / 60).
     */
    public static BigDecimal calcularCalorias(BigDecimal caloriasHora, int minutos) {
        if (caloriasHora == null || minutos <= 0) return BigDecimal.ZERO.setScale(2);
        return caloriasHora.multiply(BigDecimal.valueOf(minutos))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    public int getIdRegistro() { return idRegistro; }
    public void setIdRegistro(int idRegistro) { this.idRegistro = idRegistro; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdActividad() { return idActividad; }
    public void setIdActividad(int idActividad) { this.idActividad = idActividad; }

    public String getNombreActividad() { return nombreActividad; }
    public void setNombreActividad(String nombreActividad) { this.nombreActividad = nombreActividad; }

    public String getTipoActividad() { return tipoActividad; }
    public void setTipoActividad(String tipoActividad) { this.tipoActividad = tipoActividad; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getFechaTexto() { return fecha == null ? "" : fecha.format(FORMATO); }

    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public BigDecimal getCaloriasQuemadas() { return caloriasQuemadas; }
    public void setCaloriasQuemadas(BigDecimal caloriasQuemadas) { this.caloriasQuemadas = caloriasQuemadas; }
}
