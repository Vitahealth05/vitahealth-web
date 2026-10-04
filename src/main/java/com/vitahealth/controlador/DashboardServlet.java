package com.vitahealth.controlador;

import com.vitahealth.dao.HidratacionDAO;
import com.vitahealth.dao.PerfilDAO;
import com.vitahealth.modelo.Hidratacion;
import com.vitahealth.modelo.ResumenDia;
import com.vitahealth.modelo.Usuario;
import com.vitahealth.servicio.EstadisticasServicio;
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
 * Panel principal (prototipo 4) con el resumen del día y la semana (RF04, RF05, RF06).
 * GET /dashboard
 */
@WebServlet(name = "DashboardServlet", urlPatterns = "/dashboard")
public class DashboardServlet extends HttpServlet {

    private final HidratacionDAO hidratacionDAO = new HidratacionDAO();
    private final PerfilDAO perfilDAO = new PerfilDAO();
    private final EstadisticasServicio estadisticas = new EstadisticasServicio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
        LocalDate hoy = LocalDate.now();
        try {
            int aguaHoy = hidratacionDAO.totalDelDia(usuario.getIdUsuario(), hoy);
            List<ResumenDia> semana = estadisticas.ultimosSieteDias(usuario.getIdUsuario(), hoy);
            ResumenDia resumenHoy = semana.get(semana.size() - 1);

            int minutosSemana = 0;
            double caloriasSemana = 0;
            for (ResumenDia r : semana) {
                minutosSemana += r.getMinutos();
                caloriasSemana += r.getCalorias();
            }

            req.setAttribute("aguaHoy", aguaHoy);
            req.setAttribute("vasosHoy", Hidratacion.vasos(aguaHoy));
            req.setAttribute("porcentajeAgua", Hidratacion.porcentajeMeta(aguaHoy));
            req.setAttribute("metaAgua", Hidratacion.META_DIARIA_ML);
            req.setAttribute("hoy", resumenHoy);
            req.setAttribute("metaMinutos", 30);  // recomendación OMS: 150 min/semana ≈ 30 min/día
            req.setAttribute("porcentajeMinutos", Math.min(100, resumenHoy.getMinutos() * 100 / 30));
            req.setAttribute("semana", semana);
            req.setAttribute("maxMinutos", EstadisticasServicio.maximoMinutos(semana));
            req.setAttribute("minutosSemana", minutosSemana);
            req.setAttribute("caloriasSemana", caloriasSemana);
            req.setAttribute("perfil", perfilDAO.buscarPorUsuario(usuario.getIdUsuario()));
            req.setAttribute("seccion", "inicio");
            req.getRequestDispatcher("/WEB-INF/vistas/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Error cargando el panel", e);
        }
    }
}
