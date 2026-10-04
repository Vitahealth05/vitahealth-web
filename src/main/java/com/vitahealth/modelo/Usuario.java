package com.vitahealth.modelo;

import java.time.LocalDateTime;

/**
 * Usuario registrado en VitaHealth. Corresponde a la tabla "usuarios".
 * Los atributos son privados/protegidos y se acceden por métodos (encapsulamiento).
 */
public class Usuario extends Persona {

    private int idUsuario;
    private String contrasena;          // hash PBKDF2, nunca la contraseña en texto plano
    private LocalDateTime fechaRegistro;
    private boolean estado = true;

    public Usuario() { }

    public Usuario(String nombre, String apellido, String correo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    /** Inicial del nombre, usada como avatar en la interfaz. */
    public String getInicial() {
        return (nombre == null || nombre.isBlank()) ? "?" : nombre.substring(0, 1).toUpperCase();
    }

    @Override
    public String getRol() {
        return "Usuario";
    }
}
