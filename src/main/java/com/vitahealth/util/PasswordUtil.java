package com.vitahealth.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifrado de contraseñas con PBKDF2-HMAC-SHA256 y sal aleatoria (RNF04 / RNF1 Seguridad).
 * Formato guardado en la columna "contrasena": iteraciones:salBase64:hashBase64
 */
public final class PasswordUtil {

    private static final int ITERACIONES = 65_536;
    private static final int LONGITUD_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() { }

    public static String cifrar(String contrasena) {
        byte[] sal = new byte[16];
        RANDOM.nextBytes(sal);
        byte[] hash = pbkdf2(contrasena.toCharArray(), sal, ITERACIONES);
        return ITERACIONES + ":" + Base64.getEncoder().encodeToString(sal)
                + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String contrasena, String guardado) {
        if (contrasena == null || guardado == null) return false;
        String[] partes = guardado.split(":");
        if (partes.length != 3) return false;
        int iteraciones = Integer.parseInt(partes[0]);
        byte[] sal = Base64.getDecoder().decode(partes[1]);
        byte[] esperado = Base64.getDecoder().decode(partes[2]);
        byte[] calculado = pbkdf2(contrasena.toCharArray(), sal, iteraciones);
        return MessageDigest.isEqual(esperado, calculado);   // comparación en tiempo constante
    }

    private static byte[] pbkdf2(char[] contrasena, byte[] sal, int iteraciones) {
        try {
            PBEKeySpec spec = new PBEKeySpec(contrasena, sal, iteraciones, LONGITUD_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible cifrar la contraseña", e);
        }
    }
}
