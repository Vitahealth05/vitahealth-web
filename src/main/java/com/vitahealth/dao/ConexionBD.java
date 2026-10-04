package com.vitahealth.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fábrica de conexiones JDBC. Lee la configuración de db.properties.
 * Por defecto usa H2 en modo PostgreSQL (no requiere instalar nada);
 * para usar PostgreSQL basta con cambiar db.url, db.usuario y db.clave.
 * Una propiedad del sistema "vitahealth.db.url" sobrescribe la URL (se usa en las pruebas).
 */
public final class ConexionBD {

    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream in = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) CONFIG.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        try {
            Class.forName(CONFIG.getProperty("db.driver", "org.h2.Driver"));
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("No se encontró el driver JDBC: " + e.getMessage());
        }
    }

    private ConexionBD() { }

    public static String getUrl() {
        String url = System.getProperty("vitahealth.db.url", CONFIG.getProperty("db.url"));
        return url.replace("${user.home}", System.getProperty("user.home").replace('\\', '/'));
    }

    public static boolean esH2() {
        return getUrl().startsWith("jdbc:h2:");
    }

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(getUrl(),
                CONFIG.getProperty("db.usuario", "sa"),
                CONFIG.getProperty("db.clave", ""));
    }
}
