package com.vitahealth.dao;

import com.vitahealth.modelo.RegistroActividad;

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
 * Acceso a datos de la tabla "registro_actividad".
 */
public class RegistroActividadDAO {

    public void insertar(RegistroActividad r) throws SQLException {
        String sql = "INSERT INTO registro_actividad (id_usuario, id_actividad, fecha, duracion_minutos, calorias_quemadas) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getIdUsuario());
            ps.setInt(2, r.getIdActividad());
            ps.setDate(3, Date.valueOf(r.getFecha()));
            ps.setInt(4, r.getDuracionMinutos());
            ps.setBigDecimal(5, r.getCaloriasQuemadas());
            ps.executeUpdate();
        }
    }

    /** Lista los registros del usuario entre dos fechas (incluidas), del más reciente al más antiguo. */
    public List<RegistroActividad> listarPorRango(int idUsuario, LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT r.*, a.nombre AS nombre_actividad, a.tipo AS tipo_actividad "
                   + "FROM registro_actividad r JOIN actividades a ON a.id_actividad = r.id_actividad "
                   + "WHERE r.id_usuario = ? AND r.fecha BETWEEN ? AND ? "
                   + "ORDER BY r.fecha DESC, r.id_registro DESC";
        List<RegistroActividad> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setDate(2, Date.valueOf(desde));
            ps.setDate(3, Date.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RegistroActividad r = new RegistroActividad();
                    r.setIdRegistro(rs.getInt("id_registro"));
                    r.setIdUsuario(rs.getInt("id_usuario"));
                    r.setIdActividad(rs.getInt("id_actividad"));
                    r.setNombreActividad(rs.getString("nombre_actividad"));
                    r.setTipoActividad(rs.getString("tipo_actividad"));
                    r.setFecha(rs.getDate("fecha").toLocalDate());
                    r.setDuracionMinutos(rs.getInt("duracion_minutos"));
                    r.setCaloriasQuemadas(rs.getBigDecimal("calorias_quemadas"));
                    lista.add(r);
                }
            }
        }
        return lista;
    }

    /** Elimina un registro solo si pertenece al usuario (seguridad: cada quien sus datos). */
    public boolean eliminar(int idRegistro, int idUsuario) throws SQLException {
        String sql = "DELETE FROM registro_actividad WHERE id_registro = ? AND id_usuario = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRegistro);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() == 1;
        }
    }

    /** Minutos y calorías por día en un rango: fecha -> {minutos, calorias}. */
    public Map<LocalDate, double[]> totalesPorDia(int idUsuario, LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT fecha, SUM(duracion_minutos) AS minutos, SUM(calorias_quemadas) AS calorias "
                   + "FROM registro_actividad WHERE id_usuario = ? AND fecha BETWEEN ? AND ? GROUP BY fecha";
        Map<LocalDate, double[]> mapa = new LinkedHashMap<>();
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setDate(2, Date.valueOf(desde));
            ps.setDate(3, Date.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mapa.put(rs.getDate("fecha").toLocalDate(),
                            new double[]{rs.getInt("minutos"), rs.getDouble("calorias")});
                }
            }
        }
        return mapa;
    }
}
