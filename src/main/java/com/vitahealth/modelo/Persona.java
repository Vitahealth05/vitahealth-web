package com.vitahealth.modelo;

/**
 * Clase base abstracta con los datos comunes de una persona.
 * Aplica el principio de herencia definido en el informe de entregables
 * (Persona -> Usuario).
 */
public abstract class Persona {

    protected String nombre;
    protected String apellido;
    protected String correo;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    /** Nombre y apellido separados por un espacio. */
    public String getNombreCompleto() {
        return (nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido);
    }

    /** Cada tipo de persona describe su rol dentro del sistema (polimorfismo). */
    public abstract String getRol();
}
