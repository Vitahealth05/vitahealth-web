package com.vitahealth.controlador;

import com.vitahealth.dao.PerfilDAO;
import com.vitahealth.dao.UsuarioDAO;
import com.vitahealth.modelo.Perfil;
import com.vitahealth.modelo.Usuario;
import com.vitahealth.util.PasswordUtil;
import com.vitahealth.util.Validador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RF01 - Registro de usuarios.
 * GET  /registro : muestra el formulario "Crear cuenta".
 * POST /registro : valida los datos, cifra la contraseña y guarda el usuario.
 */
@WebServlet(name = "RegistroServlet", urlPatterns = "/registro")
public class RegistroServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PerfilDAO perfilDAO = new PerfilDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/vistas/registro.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nombre = Validador.limpiar(req.getParameter("nombre"), 80);
        String apellido = Validador.limpiar(req.getParameter("apellido"), 80);
        String correo = Validador.limpiar(req.getParameter("correo"), 120);
        String contrasena = req.getParameter("contrasena");
        String confirmar = req.getParameter("confirmar");
        boolean terminos = req.getParameter("terminos") != null;

        Map<String, String> errores = new LinkedHashMap<>();
        if (Validador.vacio(nombre)) errores.put("nombre", "El nombre es obligatorio.");
        if (Validador.vacio(apellido)) errores.put("apellido", "El apellido es obligatorio.");
        if (!Validador.correoValido(correo)) errores.put("correo", "Escribe un correo electrónico válido.");
        if (!Validador.contrasenaSegura(contrasena))
            errores.put("contrasena", "Mínimo 8 caracteres, con letras y números.");
        else if (!contrasena.equals(confirmar))
            errores.put("confirmar", "Las contraseñas no coinciden.");
        if (!terminos) errores.put("terminos", "Debes aceptar los términos y la política de privacidad.");

        try {
            if (!errores.containsKey("correo") && usuarioDAO.existeCorreo(correo)) {
                errores.put("correo", "Este correo ya está registrado.");
            }
            if (!errores.isEmpty()) {
                req.setAttribute("errores", errores);
                req.setAttribute("nombre", nombre);
                req.setAttribute("apellido", apellido);
                req.setAttribute("correo", correo);
                req.getRequestDispatcher("/WEB-INF/vistas/registro.jsp").forward(req, resp);
                return;
            }
            Usuario usuario = new Usuario(nombre, apellido, correo);
            usuario.setContrasena(PasswordUtil.cifrar(contrasena));
            int id = usuarioDAO.insertar(usuario);

            Perfil perfil = new Perfil();          // perfil vacío, se completa luego en "Mi perfil"
            perfil.setIdUsuario(id);
            perfilDAO.guardar(perfil);

            // Patrón POST-Redirect-GET: evita registros duplicados al recargar la página
            resp.sendRedirect(req.getContextPath() + "/login?registrado=1");
        } catch (SQLException e) {
            throw new ServletException("Error registrando el usuario", e);
        }
    }
}
