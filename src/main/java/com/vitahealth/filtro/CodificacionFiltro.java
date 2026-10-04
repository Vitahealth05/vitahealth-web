package com.vitahealth.filtro;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

/**
 * Fuerza UTF-8 en peticiones y respuestas para que tildes y eñes de los formularios lleguen bien.
 */
@WebFilter(filterName = "CodificacionFiltro", urlPatterns = "/*")
public class CodificacionFiltro implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain cadena)
            throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        cadena.doFilter(req, resp);
    }
}
