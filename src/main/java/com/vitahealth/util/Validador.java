package com.vitahealth.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Validaciones de los datos que llegan desde los formularios HTML (integridad de dominio).
 */
public final class Validador {

    private static final Pattern CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");

    private Validador() { }

    public static boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static boolean correoValido(String correo) {
        return !vacio(correo) && correo.length() <= 120 && CORREO.matcher(correo.trim()).matches();
    }

    /** Mínimo 8 caracteres, con al menos una letra y un número. */
    public static boolean contrasenaSegura(String contrasena) {
        if (contrasena == null || contrasena.length() < 8) return false;
        boolean letra = false, numero = false;
        for (char c : contrasena.toCharArray()) {
            if (Character.isLetter(c)) letra = true;
            if (Character.isDigit(c)) numero = true;
        }
        return letra && numero;
    }

    /** Convierte a entero; devuelve null si el texto no es un número válido. */
    public static Integer entero(String valor) {
        try {
            return vacio(valor) ? null : Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Convierte a decimal aceptando coma o punto; null si no es válido. */
    public static BigDecimal decimal(String valor) {
        try {
            return vacio(valor) ? null : new BigDecimal(valor.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Convierte una fecha yyyy-MM-dd (input type="date"); null si no es válida. */
    public static LocalDate fecha(String valor) {
        try {
            return vacio(valor) ? null : LocalDate.parse(valor.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /** Recorta espacios y limita la longitud de un texto. */
    public static String limpiar(String valor, int max) {
        if (valor == null) return null;
        String v = valor.trim();
        return v.length() > max ? v.substring(0, max) : v;
    }
}
