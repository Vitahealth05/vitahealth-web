package com.vitahealth.controlador;

import com.vitahealth.dao.HidratacionDAO;
import com.vitahealth.modelo.Hidratacion;
import com.vitahealth.modelo.ResumenDia;
import com.vitahealth.modelo.Usuario;
import com.vitahealth.servicio.EstadisticasServicio;
import com.vitahealth.util.Validador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * RF04 - Seguimiento de hidratación (prototipo 5).
 * GET  /hidratacion            : progreso del día, vasos y semana.
 * POST /hidratacion accion=agregar  cantidad=ml : registra agua.
 * POST /hidratacion accion=deshacer             : elimina el último registro del día.
 */
@WebServlet(name = "HidratacionServlet", urlPatterns = "/hidratacion")
public class HidratacionServlet extends HttpServlet {

    private final HidratacionDAO hidratacionDAO = new HidratacionDAO();
    private final EstadisticasServicio estadisticas = new EstadisticasServicio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
        LocalDate hoy = LocalDate.now();
        try {
            int total = hidratacionDAO.totalDelDia(usuario.getIdUsuario(), hoy);
            List<ResumenDia> semana = estadisticas.ultimosSieteDias(usuario.getIdUsuario(), hoy);
            req.setAttribute("totalMl", total);
            req.setAttribute("vasos", Hidratacion.vasos(total));
            req.setAttribute("metaVasos", Hidratacion.META_DIARIA_ML / Hidratacion.ML_POR_VASO);
            req.setAttribute("metaMl", Hidratacion.META_DIARIA_ML);
            req.setAttribute("porcentaje", Hidratacion.porcentajeMeta(total));
            req.setAttribute("registrosHoy", hidratacionDAO.listarDelDia(usuario.getIdUsuario(), hoy));
            req.setAttribute("semana", semana);
            req.setAttribute("seccion", "hidratacion");
            req.getRequestDispatcher("/WEB-INF/vistas/hidratacion.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Error consultando la hidratación", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
        String accion = req.getParameter("accion");
        try {
            if ("deshacer".equals(accion)) {
                if (hidratacionDAO.eliminarUltimo(usuario.getIdUsuario(), LocalDate.now()))
                    req.getSession().setAttribute("mensaje", "Se eliminó el último registro de agua.");
            } else {
                Integer ml = Validador.entero(req.getParameter("cantidad"));
                if (ml == null || ml < 50 || ml > 3000) {
                    req.getSession().setAttribute("error", "La cantidad debe estar entre 50 y 3000 ml.");
                } else {
                    hidratacionDAO.insertar(new Hidratacion(usuario.getIdUsuario(), LocalDate.now(), ml));
                    req.getSession().setAttribute("mensaje", "¡Bien! Registraste " + ml + " ml de agua.");
                }
            }
            resp.sendRedirect(req.getContextPath() + "/hidratacion");
        } catch (SQLException e) {
            throw new ServletException("Error registrando la hidratación", e);
        }
    }
}
