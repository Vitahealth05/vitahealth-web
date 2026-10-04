package com.vitahealth.dao;

import com.vitahealth.modelo.Hidratacion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Acceso a datos de la tabla "hidratacion".
 */
public class HidratacionDAO {

    public void insertar(Hidratacion h) throws SQLException {
        String sql = "INSERT INTO hidratacion (id_usuario, fecha, cantidad_ml) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, h.getIdUsuario());
            ps.setDate(2, Date.valueOf(h.getFecha()));
            ps.setInt(3, h.getCantidadMl());
            ps.executeUpdate();
        }
    }

    public int totalDelDia(int idUsuario, LocalDate fecha) throws SQLException {
        String sql = "SELECT COALESCE(SUM(cantidad_ml), 0) FROM hidratacion WHERE id_usuario = ? AND fecha = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public List<Hidratacion> listarDelDia(int idUsuario, LocalDate fecha) throws SQLException {
        String sql = "SELECT * FROM hidratacion WHERE id_usuario = ? AND fecha = ? ORDER BY id_hidratacion DESC";
        List<Hidratacion> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Hidratacion h = new Hidratacion(idUsuario, rs.getDate("fecha").toLocalDate(), rs.getInt("cantidad_ml"));
                    h.setIdHidratacion(rs.getInt("id_hidratacion"));
                    lista.add(h);
                }
            }
        }
        return lista;
    }

    /** Elimina el último registro del día (botón "deshacer"). */
    public boolean eliminarUltimo(int idUsuario, LocalDate fecha) throws SQLException {
        List<Hidratacion> hoy = listarDelDia(idUsuario, fecha);
        if (hoy.isEmpty()) return false;
        String sql = "DELETE FROM hidratacion WHERE id_hidratacion = ? AND id_usuario = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hoy.get(0).getIdHidratacion());
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() == 1;
        }
    }

    /** Total de ml por día en un rango: fecha -> ml. */
    public Map<LocalDate, Integer> totalesPorDia(int idUsuario, LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT fecha, SUM(cantidad_ml) AS total FROM hidratacion "
                   + "WHERE id_usuario = ? AND fecha BETWEEN ? AND ? GROUP BY fecha";
        Map<LocalDate, Integer> mapa = new LinkedHashMap<>();
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setDate(2, Date.valueOf(desde));
            ps.setDate(3, Date.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) mapa.put(rs.getDate("fecha").toLocalDate(), rs.getInt("total"));
            }
        }
        return mapa;
    }
}
