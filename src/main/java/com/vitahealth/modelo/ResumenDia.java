package com.vitahealth.modelo;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Totales de un día (minutos de actividad, calorías y agua) para estadísticas (RF06).
 */
public class ResumenDia {

    private final LocalDate fecha;
    private int minutos;
    private double calorias;
    private int aguaMl;

    public ResumenDia(LocalDate fecha) { this.fecha = fecha; }

    public LocalDate getFecha() { return fecha; }

    /** Inicial del día de la semana en español (L, M, X...). */
    public String getDiaCorto() {
        String d = fecha.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es-CO"));
        return d.substring(0, 1).toUpperCase() + d.substring(1, Math.min(3, d.length())).replace(".", "");
    }

    public int getMinutos() { return minutos; }
    public void setMinutos(int minutos) { this.minutos = minutos; }

    public double getCalorias() { return calorias; }
    public void setCalorias(double calorias) { this.calorias = calorias; }

    public int getAguaMl() { return aguaMl; }
    public void setAguaMl(int aguaMl) { this.aguaMl = aguaMl; }
}
