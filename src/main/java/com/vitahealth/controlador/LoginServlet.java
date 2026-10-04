package com.vitahealth.controlador;

import com.vitahealth.dao.UsuarioDAO;
import com.vitahealth.modelo.Usuario;
import com.vitahealth.util.PasswordUtil;
import com.vitahealth.util.Validador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/**
 * RF02 - Inicio de sesión.
 * GET  /login : muestra el formulario.
 * POST /login : valida correo y contraseña y crea la sesión.
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession sesion = req.getSession(false);
        if (sesion != null && sesion.getAttribute("usuario") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        // Si el usuario marcó "Recordar mi correo", se precarga desde la cookie
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("vh_correo".equals(c.getName())) req.setAttribute("correo", c.getValue());
            }
        }
        req.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String correo = Validador.limpiar(req.getParameter("correo"), 120);
        String contrasena = req.getParameter("contrasena");
        boolean recordar = req.getParameter("recordar") != null;
        req.setAttribute("correo", correo);

        if (!Validador.correoValido(correo) || Validador.vacio(contrasena)) {
            req.setAttribute("error", "Ingresa un correo válido y tu contraseña.");
            req.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(req, resp);
            return;
        }
        try {
            Usuario usuario = usuarioDAO.buscarPorCorreo(correo);
            if (usuario == null || !usuario.isEstado() || !PasswordUtil.verificar(contrasena, usuario.getContrasena())) {
                req.setAttribute("error", "Correo o contraseña incorrectos.");
                req.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(req, resp);
                return;
            }
            // Se renueva la sesión para evitar fijación de sesión
            HttpSession anterior = req.getSession(false);
            if (anterior != null) anterior.invalidate();
            HttpSession sesion = req.getSession(true);
            usuario.setContrasena(null);           // no se guarda el hash en la sesión
            sesion.setAttribute("usuario", usuario);

            Cookie cookie = new Cookie("vh_correo", recordar ? usuario.getCorreo() : "");
            cookie.setMaxAge(recordar ? 60 * 60 * 24 * 30 : 0);
            cookie.setHttpOnly(true);
            cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
            resp.addCookie(cookie);

            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            throw new ServletException("Error consultando el usuario", e);
        }
    }
}
