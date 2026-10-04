package com.vitahealth.filtro;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Protege las páginas internas: si no hay usuario en sesión redirige al inicio de sesión
 * (restricción "El usuario debe iniciar sesión previamente" de los prototipos 3 a 6).
 */
@WebFilter(filterName = "AutenticacionFiltro",
           urlPatterns = {"/dashboard", "/hidratacion", "/actividad", "/perfil"})
public class AutenticacionFiltro implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain cadena)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession sesion = req.getSession(false);

        if (sesion == null || sesion.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login?requiere=1");
            return;
        }
        // Evita que el navegador muestre páginas privadas desde caché después de cerrar sesión
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        cadena.doFilter(request, response);
    }
}
