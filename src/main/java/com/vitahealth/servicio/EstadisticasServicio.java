package com.vitahealth.servicio;

import com.vitahealth.dao.HidratacionDAO;
import com.vitahealth.dao.RegistroActividadDAO;
import com.vitahealth.modelo.ResumenDia;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RF06 - Estadísticas: arma el resumen de los últimos 7 días combinando actividad e hidratación.
 */
public class EstadisticasServicio {

    private final RegistroActividadDAO actividadDAO = new RegistroActividadDAO();
    private final HidratacionDAO hidratacionDAO = new HidratacionDAO();

    public List<ResumenDia> ultimosSieteDias(int idUsuario, LocalDate hoy) throws SQLException {
        LocalDate desde = hoy.minusDays(6);
        Map<LocalDate, double[]> actividad = actividadDAO.totalesPorDia(idUsuario, desde, hoy);
        Map<LocalDate, Integer> agua = hidratacionDAO.totalesPorDia(idUsuario, desde, hoy);

        List<ResumenDia> semana = new ArrayList<>();
        for (LocalDate d = desde; !d.isAfter(hoy); d = d.plusDays(1)) {
            ResumenDia r = new ResumenDia(d);
            double[] a = actividad.get(d);
            if (a != null) {
                r.setMinutos((int) a[0]);
                r.setCalorias(a[1]);
            }
            r.setAguaMl(agua.getOrDefault(d, 0));
            semana.add(r);
        }
        return semana;
    }

    /** Valor máximo de minutos de la semana (mínimo 30) para escalar las barras del gráfico. */
    public static int maximoMinutos(List<ResumenDia> semana) {
        int max = 30;
        for (ResumenDia r : semana) max = Math.max(max, r.getMinutos());
        return max;
    }
}
