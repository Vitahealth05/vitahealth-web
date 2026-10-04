package com.vitahealth.modelo;

import java.time.LocalDate;

/**
 * Registro de consumo de agua. Corresponde a la tabla "hidratacion" (RF04).
 */
public class Hidratacion {

    /** Meta diaria por defecto: 8 vasos de 250 ml (prototipo 5). */
    public static final int META_DIARIA_ML = 2000;
    public static final int ML_POR_VASO = 250;

    private int idHidratacion;
    private int idUsuario;
    private LocalDate fecha;
    private int cantidadMl;

    public Hidratacion() { }

    public Hidratacion(int idUsuario, LocalDate fecha, int cantidadMl) {
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.cantidadMl = cantidadMl;
    }

    /** Porcentaje de la meta alcanzado (0-100, no supera 100). */
    public static int porcentajeMeta(int totalMl) {
        if (totalMl <= 0) return 0;
        return Math.min(100, Math.round(totalMl * 100f / META_DIARIA_ML));
    }

    /** Número de vasos (de 250 ml) que equivalen a una cantidad. */
    public static int vasos(int totalMl) {
        return totalMl / ML_POR_VASO;
    }

    public int getIdHidratacion() { return idHidratacion; }
    public void setIdHidratacion(int idHidratacion) { this.idHidratacion = idHidratacion; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public int getCantidadMl() { return cantidadMl; }
    public void setCantidadMl(int cantidadMl) { this.cantidadMl = cantidadMl; }
}
