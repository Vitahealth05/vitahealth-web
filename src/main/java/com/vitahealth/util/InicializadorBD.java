package com.vitahealth.util;

import com.vitahealth.dao.ConexionBD;
import com.vitahealth.dao.EsquemaBD;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Collections;

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

    /** Al detener la aplicación se liberan los drivers JDBC registrados (evita fugas de memoria). */
    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        for (Driver d : Collections.list(DriverManager.getDrivers())) {
            if (d.getClass().getClassLoader() == getClass().getClassLoader()) {
                try { DriverManager.deregisterDriver(d); } catch (Exception ignorada) { }
            }
        }
    }
}
