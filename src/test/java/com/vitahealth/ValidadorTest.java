package com.vitahealth;

import com.vitahealth.util.Validador;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validador - datos de formularios")
class ValidadorTest {

    @Test
    @DisplayName("Correos válidos e inválidos")
    void correos() {
        assertTrue(Validador.correoValido("laura.gomez@correo.com"));
        assertTrue(Validador.correoValido("aprendiz+sena@soy.sena.edu.co"));
        assertFalse(Validador.correoValido("laura@"));
        assertFalse(Validador.correoValido("sin-arroba.com"));
        assertFalse(Validador.correoValido(""));
        assertFalse(Validador.correoValido(null));
    }

    @Test
    @DisplayName("Contraseña segura: 8+ caracteres con letras y números")
    void contrasenas() {
        assertTrue(Validador.contrasenaSegura("Vita2026"));
        assertFalse(Validador.contrasenaSegura("corta1"));
        assertFalse(Validador.contrasenaSegura("soloLetrasAqui"));
        assertFalse(Validador.contrasenaSegura("12345678"));
    }

    @Test
    @DisplayName("Conversión de números y fechas desde texto")
    void conversiones() {
        assertEquals(30, Validador.entero(" 30 "));
        assertNull(Validador.entero("treinta"));
        assertEquals(new BigDecimal("62.5"), Validador.decimal("62,5"));
        assertNull(Validador.decimal("abc"));
        assertEquals(LocalDate.of(2026, 10, 4), Validador.fecha("2026-10-04"));
        assertNull(Validador.fecha("04/10/2026"));
    }

    @Test
    @DisplayName("Limpiar recorta espacios y longitud")
    void limpiar() {
        assertEquals("Laura", Validador.limpiar("  Laura  ", 80));
        assertEquals("abc", Validador.limpiar("abcdef", 3));
        assertNull(Validador.limpiar(null, 5));
    }
}
