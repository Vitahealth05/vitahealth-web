package com.vitahealth.dao;

import com.vitahealth.modelo.Perfil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Acceso a datos de la tabla "perfiles".
 */
public class PerfilDAO {

    public Perfil buscarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM perfiles WHERE id_usuario = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Perfil p = new Perfil();
                p.setIdPerfil(rs.getInt("id_perfil"));
                p.setIdUsuario(idUsuario);
                Date f = rs.getDate("fecha_nacimiento");
                if (f != null) p.setFechaNacimiento(f.toLocalDate());
                p.setPesoKg(rs.getBigDecimal("peso_kg"));
                p.setAlturaCm(rs.getBigDecimal("altura_cm"));
                p.setObjetivo(rs.getString("objetivo"));
                return p;
            }
        }
    }

    /** Crea el perfil si no existe o lo actualiza si ya existe. */
    public void guardar(Perfil p) throws SQLException {
        boolean existe = buscarPorUsuario(p.getIdUsuario()) != null;
        String sql = existe
                ? "UPDATE perfiles SET fecha_nacimiento = ?, peso_kg = ?, altura_cm = ?, objetivo = ? WHERE id_usuario = ?"
                : "INSERT INTO perfiles (fecha_nacimiento, peso_kg, altura_cm, objetivo, id_usuario) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (p.getFechaNacimiento() != null) ps.setDate(1, Date.valueOf(p.getFechaNacimiento()));
            else ps.setNull(1, Types.DATE);
            ps.setBigDecimal(2, p.getPesoKg());
            ps.setBigDecimal(3, p.getAlturaCm());
            ps.setString(4, p.getObjetivo());
            ps.setInt(5, p.getIdUsuario());
            ps.executeUpdate();
        }
    }
}
