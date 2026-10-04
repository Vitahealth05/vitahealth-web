package com.vitahealth;

import com.vitahealth.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PasswordUtil - cifrado de contraseñas")
class PasswordUtilTest {

    @Test
    @DisplayName("El hash no contiene la contraseña en texto plano")
    void hashNoContieneTextoPlano() {
        String hash = PasswordUtil.cifrar("Vita2026");
        assertFalse(hash.contains("Vita2026"));
        assertEquals(3, hash.split(":").length);
    }

    @Test
    @DisplayName("Verifica la contraseña correcta y rechaza una incorrecta")
    void verificaContrasena() {
        String hash = PasswordUtil.cifrar("Vita2026");
        assertTrue(PasswordUtil.verificar("Vita2026", hash));
        assertFalse(PasswordUtil.verificar("vita2026", hash));
        assertFalse(PasswordUtil.verificar(null, hash));
    }

    @Test
    @DisplayName("Dos cifrados de la misma clave son distintos (sal aleatoria)")
    void salAleatoria() {
        assertNotEquals(PasswordUtil.cifrar("Vita2026"), PasswordUtil.cifrar("Vita2026"));
    }
}
