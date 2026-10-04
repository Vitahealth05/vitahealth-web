package com.vitahealth.dao;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Crea las tablas (schema.sql) si no existen y carga el catálogo inicial de actividades.
 */
public final class EsquemaBD {

    private static final Object[][] ACTIVIDADES = {
        {"Caminata", "Caminar a paso moderado", "Cardio", 280},
        {"Trote", "Correr a ritmo suave", "Cardio", 600},
        {"Ciclismo", "Bicicleta a ritmo moderado", "Cardio", 480},
        {"Natación", "Nado continuo", "Cardio", 500},
        {"Entrenamiento de fuerza", "Pesas o peso corporal", "Fuerza", 320},
        {"Yoga", "Posturas y respiración", "Flexibilidad", 180},
        {"Baile", "Rumba, salsa o aeróbicos", "Cardio", 400},
        {"Estiramientos", "Rutina de movilidad", "Flexibilidad", 150},
    };

    private EsquemaBD() { }

    public static void inicializar() throws SQLException, IOException {
        String script;
        try (InputStream in = EsquemaBD.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (in == null) throw new IOException("No se encontró schema.sql");
            script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        try (Connection con = ConexionBD.obtener(); Statement st = con.createStatement()) {
            for (String sentencia : script.split(";")) {
                String limpia = sentencia.replaceAll("(?m)^--.*$", "").trim();
                if (!limpia.isEmpty()) st.execute(limpia);
            }
            cargarActividades(con);
        }
    }

    private static void cargarActividades(Connection con) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM actividades")) {
            rs.next();
            if (rs.getInt(1) > 0) return;
        }
        String sql = "INSERT INTO actividades (nombre, descripcion, tipo, calorias_hora) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Object[] a : ACTIVIDADES) {
                ps.setString(1, (String) a[0]);
                ps.setString(2, (String) a[1]);
                ps.setString(3, (String) a[2]);
                ps.setBigDecimal(4, BigDecimal.valueOf((Integer) a[3]));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
