package com.vitahealth.controlador;

import com.vitahealth.dao.PerfilDAO;
import com.vitahealth.dao.UsuarioDAO;
import com.vitahealth.modelo.Perfil;
import com.vitahealth.modelo.Usuario;
import com.vitahealth.util.Validador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RF03 / RF08 - Perfil del usuario (prototipo 8): datos personales, peso, altura y objetivo.
 * GET  /perfil : muestra el formulario con los datos guardados.
 * POST /perfil : valida y actualiza.
 */
@WebServlet(name = "PerfilServlet", urlPatterns = "/perfil")
public class PerfilServlet extends HttpServlet {

    private final PerfilDAO perfilDAO = new PerfilDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
        try {
            Perfil perfil = perfilDAO.buscarPorUsuario(usuario.getIdUsuario());
            if (perfil == null) {
                perfil = new Perfil();
                perfil.setIdUsuario(usuario.getIdUsuario());
            }
            req.setAttribute("perfil", perfil);
            req.setAttribute("seccion", "perfil");
            req.getRequestDispatcher("/WEB-INF/vistas/perfil.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Error cargando el perfil", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
        String nombre = Validador.limpiar(req.getParameter("nombre"), 80);
        String apellido = Validador.limpiar(req.getParameter("apellido"), 80);
        String textoFecha = req.getParameter("fechaNacimiento");
        String textoPeso = req.getParameter("peso");
        String textoAltura = req.getParameter("altura");
        LocalDate fecha = Validador.fecha(textoFecha);
        BigDecimal peso = Validador.decimal(textoPeso);
        BigDecimal altura = Validador.decimal(textoAltura);

        Map<String, String> errores = new LinkedHashMap<>();
        if (Validador.vacio(nombre)) errores.put("nombre", "El nombre es obligatorio.");
        if (Validador.vacio(apellido)) errores.put("apellido", "El apellido es obligatorio.");
        if (!Validador.vacio(textoFecha) && (fecha == null || fecha.isAfter(LocalDate.now().minusYears(13))))
            errores.put("fechaNacimiento", "Revisa la fecha (debes tener al menos 13 años).");
        if (!Validador.vacio(textoPeso) && (peso == null || peso.doubleValue() < 20 || peso.doubleValue() > 400))
            errores.put("peso", "El peso debe estar entre 20 y 400 kg.");
        if (!Validador.vacio(textoAltura) && (altura == null || altura.doubleValue() < 50 || altura.doubleValue() > 250))
            errores.put("altura", "La altura debe estar entre 50 y 250 cm.");

        Perfil perfil = new Perfil();
        perfil.setIdUsuario(usuario.getIdUsuario());
        perfil.setFechaNacimiento(fecha);
        perfil.setPesoKg(peso);
        perfil.setAlturaCm(altura);
        perfil.setObjetivo(Validador.limpiar(req.getParameter("objetivo"), 120));

        if (!errores.isEmpty()) {
            req.setAttribute("errores", errores);
            req.setAttribute("perfil", perfil);
            req.setAttribute("seccion", "perfil");
            req.getRequestDispatcher("/WEB-INF/vistas/perfil.jsp").forward(req, resp);
            return;
        }
        try {
            usuarioDAO.actualizarNombre(usuario.getIdUsuario(), nombre, apellido);
            perfilDAO.guardar(perfil);
            usuario.setNombre(nombre);       // se actualiza también el objeto en sesión
            usuario.setApellido(apellido);
            req.getSession().setAttribute("mensaje", "Perfil actualizado correctamente.");
            resp.sendRedirect(req.getContextPath() + "/perfil");
        } catch (SQLException e) {
            throw new ServletException("Error guardando el perfil", e);
        }
    }
}
