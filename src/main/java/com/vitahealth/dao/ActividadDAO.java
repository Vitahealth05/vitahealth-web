package com.vitahealth.dao;

import com.vitahealth.modelo.Actividad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Consulta del catálogo de la tabla "actividades".
 */
public class ActividadDAO {

    public List<Actividad> listar() throws SQLException {
        List<Actividad> lista = new ArrayList<>();
        String sql = "SELECT * FROM actividades ORDER BY nombre";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Actividad buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM actividades WHERE id_actividad = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    private Actividad mapear(ResultSet rs) throws SQLException {
        Actividad a = new Actividad(rs.getInt("id_actividad"), rs.getString("nombre"),
                rs.getString("tipo"), rs.getBigDecimal("calorias_hora"));
        a.setDescripcion(rs.getString("descripcion"));
        return a;
    }
}
