package com.vitahealth.util;

import com.vitahealth.dao.ConexionBD;
import com.vitahealth.dao.EsquemaBD;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Al desplegar la aplicación crea las tablas (si no existen) y el catálogo de actividades.
 */
@WebListener
public class InicializadorBD implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        try {
            EsquemaBD.inicializar();
            evento.getServletContext().log("VitaHealth: base de datos lista en " + ConexionBD.getUrl());
        } catch (Exception e) {
            evento.getServletContext().log("VitaHealth: error inicializando la base de datos", e);
        }
    }
}
