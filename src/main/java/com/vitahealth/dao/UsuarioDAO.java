package com.vitahealth.dao;

import com.vitahealth.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Acceso a datos de la tabla "usuarios" (patrón DAO).
 */
public class UsuarioDAO {

    /** Inserta el usuario y devuelve el id generado. */
    public int insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, apellido, correo, contrasena) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_usuario"})) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getCorreo().toLowerCase());
            ps.setString(4, u.getContrasena());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                u.setIdUsuario(rs.getInt(1));
                return u.getIdUsuario();
            }
        }
    }

    public Usuario buscarPorCorreo(String correo) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE correo = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public boolean existeCorreo(String correo) throws SQLException {
        return buscarPorCorreo(correo) != null;
    }

    public void actualizarNombre(int idUsuario, String nombre, String apellido) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ? WHERE id_usuario = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setInt(3, idUsuario);
            ps.executeUpdate();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario(rs.getString("nombre"), rs.getString("apellido"), rs.getString("correo"));
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setContrasena(rs.getString("contrasena"));
        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) u.setFechaRegistro(ts.toLocalDateTime());
        u.setEstado(rs.getBoolean("estado"));
        return u;
    }
}
