package com.vitahealth;

import com.vitahealth.modelo.Hidratacion;
import com.vitahealth.modelo.Perfil;
import com.vitahealth.modelo.RegistroActividad;
import com.vitahealth.modelo.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cálculos de salud (IMC, calorías, hidratación)")
class CalculosSaludTest {

    @Test
    @DisplayName("IMC de 62.5 kg y 165 cm = 23.0 (peso saludable)")
    void imc() {
        Perfil p = new Perfil();
        p.setPesoKg(new BigDecimal("62.5"));
        p.setAlturaCm(new BigDecimal("165"));
        assertEquals(new BigDecimal("23.0"), p.getImc());
        assertEquals("Peso saludable", p.getClasificacionImc());
    }

    @Test
    @DisplayName("IMC sin datos devuelve null")
    void imcSinDatos() {
        Perfil p = new Perfil();
        assertNull(p.getImc());
        assertEquals("Sin datos", p.getClasificacionImc());
    }

    @Test
    @DisplayName("Calorías: caminata 280 kcal/h durante 30 min = 140 kcal")
    void calorias() {
        assertEquals(new BigDecimal("140.00"),
                RegistroActividad.calcularCalorias(new BigDecimal("280"), 30));
        assertEquals(new BigDecimal("0.00"),
                RegistroActividad.calcularCalorias(new BigDecimal("280"), 0));
    }

    @Test
    @DisplayName("Hidratación: 1500 ml = 6 vasos = 75 % de la meta")
    void hidratacion() {
        assertEquals(6, Hidratacion.vasos(1500));
        assertEquals(75, Hidratacion.porcentajeMeta(1500));
        assertEquals(100, Hidratacion.porcentajeMeta(5000));   // no supera 100 %
        assertEquals(0, Hidratacion.porcentajeMeta(0));
    }

    @Test
    @DisplayName("Usuario hereda de Persona y define su rol (herencia y polimorfismo)")
    void herencia() {
        Usuario u = new Usuario("Laura", "Gómez", "laura@correo.com");
        assertEquals("Laura Gómez", u.getNombreCompleto());
        assertEquals("Usuario", u.getRol());
        assertEquals("L", u.getInicial());
    }
}
