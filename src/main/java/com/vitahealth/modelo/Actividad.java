package com.vitahealth.modelo;

import java.math.BigDecimal;

/**
 * Catálogo de actividades físicas. Corresponde a la tabla "actividades".
 */
public class Actividad {

    private int idActividad;
    private String nombre;
    private String descripcion;
    private String tipo;
    private BigDecimal caloriasHora;

    public Actividad() { }

    public Actividad(int idActividad, String nombre, String tipo, BigDecimal caloriasHora) {
        this.idActividad = idActividad;
        this.nombre = nombre;
        this.tipo = tipo;
        this.caloriasHora = caloriasHora;
    }

    public int getIdActividad() { return idActividad; }
    public void setIdActividad(int idActividad) { this.idActividad = idActividad; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getCaloriasHora() { return caloriasHora; }
    public void setCaloriasHora(BigDecimal caloriasHora) { this.caloriasHora = caloriasHora; }
}
