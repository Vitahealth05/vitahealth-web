package com.vitahealth.controlador;

import com.vitahealth.dao.ActividadDAO;
import com.vitahealth.dao.RegistroActividadDAO;
import com.vitahealth.modelo.Actividad;
import com.vitahealth.modelo.RegistroActividad;
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
import java.util.List;

/**
 * RF05 - Registro de actividad física (prototipo 6).
 * GET  /actividad?desde=AAAA-MM-DD&hasta=AAAA-MM-DD : historial filtrado (formulario con method="get").
 * POST /actividad accion=registrar : guarda una actividad (formulario con method="post").
 * POST /actividad accion=eliminar  : elimina un registro propio.
 */
@WebServlet(name = "ActividadServlet", urlPatterns = "/actividad")
public class ActividadServlet extends HttpServlet {

    private final ActividadDAO actividadDAO = new ActividadDAO();
    private final RegistroActividadDAO registroDAO = new RegistroActividadDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
        LocalDate hoy = LocalDate.now();

        // Parámetros del filtro (llegan en la URL porque el formulario usa GET)
        LocalDate desde = Validador.fecha(req.getParameter("desde"));
        LocalDate hasta = Validador.fecha(req.getParameter("hasta"));
        if (hasta == null) hasta = hoy;
        if (desde == null) desde = hasta.minusDays(6);
        if (desde.isAfter(hasta)) { LocalDate t = desde; desde = hasta; hasta = t; }

        try {
            List<RegistroActividad> registros = registroDAO.listarPorRango(usuario.getIdUsuario(), desde, hasta);
            int totalMinutos = 0;
            BigDecimal totalCalorias = BigDecimal.ZERO;
            for (RegistroActividad r : registros) {
                totalMinutos += r.getDuracionMinutos();
                totalCalorias = totalCalorias.add(r.getCaloriasQuemadas());
            }
            req.setAttribute("catalogo", actividadDAO.listar());
            req.setAttribute("registros", registros);
            req.setAttribute("totalMinutos", totalMinutos);
            req.setAttribute("totalCalorias", totalCalorias);
            req.setAttribute("desde", desde);
            req.setAttribute("hasta", hasta);
            req.setAttribute("hoy", hoy);
            req.setAttribute("seccion", "actividad");
            req.getRequestDispatcher("/WEB-INF/vistas/actividad.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Error consultando actividades", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
        String accion = req.getParameter("accion");
        try {
            if ("eliminar".equals(accion)) {
                Integer id = Validador.entero(req.getParameter("idRegistro"));
                if (id != null && registroDAO.eliminar(id, usuario.getIdUsuario()))
                    req.getSession().setAttribute("mensaje", "Actividad eliminada.");
            } else {
                registrar(req, usuario);
            }
            resp.sendRedirect(req.getContextPath() + "/actividad");
        } catch (SQLException e) {
            throw new ServletException("Error guardando la actividad", e);
        }
    }

    private void registrar(HttpServletRequest req, Usuario usuario) throws SQLException {
        Integer idActividad = Validador.entero(req.getParameter("idActividad"));
        Integer minutos = Validador.entero(req.getParameter("duracion"));
        LocalDate fecha = Validador.fecha(req.getParameter("fecha"));
        if (fecha == null) fecha = LocalDate.now();

        Actividad actividad = idActividad == null ? null : actividadDAO.buscarPorId(idActividad);
        if (actividad == null) {
            req.getSession().setAttribute("error", "Selecciona una actividad del listado.");
            return;
        }
        if (minutos == null || minutos < 1 || minutos > 600) {
            req.getSession().setAttribute("error", "La duración debe estar entre 1 y 600 minutos.");
            return;
        }
        if (fecha.isAfter(LocalDate.now())) {
            req.getSession().setAttribute("error", "No puedes registrar actividades en fechas futuras.");
            return;
        }
        RegistroActividad r = new RegistroActividad();
        r.setIdUsuario(usuario.getIdUsuario());
        r.setIdActividad(actividad.getIdActividad());
        r.setFecha(fecha);
        r.setDuracionMinutos(minutos);
        r.setCaloriasQuemadas(RegistroActividad.calcularCalorias(actividad.getCaloriasHora(), minutos));
        registroDAO.insertar(r);
        req.getSession().setAttribute("mensaje",
                actividad.getNombre() + " registrada: " + minutos + " min, " + r.getCaloriasQuemadas() + " kcal.");
    }
}
