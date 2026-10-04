package com.vitahealth;

import com.vitahealth.dao.ActividadDAO;
import com.vitahealth.dao.EsquemaBD;
import com.vitahealth.dao.HidratacionDAO;
import com.vitahealth.dao.PerfilDAO;
import com.vitahealth.dao.RegistroActividadDAO;
import com.vitahealth.dao.UsuarioDAO;
import com.vitahealth.modelo.Actividad;
import com.vitahealth.modelo.Hidratacion;
import com.vitahealth.modelo.Perfil;
import com.vitahealth.modelo.RegistroActividad;
import com.vitahealth.modelo.Usuario;
import com.vitahealth.util.PasswordUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración de la capa DAO contra una base H2 en memoria (modo PostgreSQL)
 * creada con el mismo schema.sql que usa la aplicación.
 */
@DisplayName("DAO - integración con base de datos H2 en memoria")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DaoIntegracionTest {

    private static int idUsuario;

    @BeforeAll
    static void crearBase() throws Exception {
        System.setProperty("vitahealth.db.url",
                "jdbc:h2:mem:vitahealth_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        EsquemaBD.inicializar();
    }

    @Test @Order(1)
    @DisplayName("Inserta un usuario y lo encuentra por correo")
    void insertarUsuario() throws SQLException {
        UsuarioDAO dao = new UsuarioDAO();
        Usuario u = new Usuario("Laura", "Gómez", "Laura@Correo.com");
        u.setContrasena(PasswordUtil.cifrar("Vita2026"));
        idUsuario = dao.insertar(u);
        assertTrue(idUsuario > 0);

        Usuario encontrado = dao.buscarPorCorreo("laura@correo.com");
        assertNotNull(encontrado);
        assertEquals("Laura", encontrado.getNombre());
        assertTrue(PasswordUtil.verificar("Vita2026", encontrado.getContrasena()));
    }

    @Test @Order(2)
    @DisplayName("El correo es único (restricción uq_usuarios_correo)")
    void correoUnico() {
        Usuario repetido = new Usuario("Otra", "Persona", "laura@correo.com");
        repetido.setContrasena("x");
        assertThrows(SQLException.class, () -> new UsuarioDAO().insertar(repetido));
    }

    @Test @Order(3)
    @DisplayName("Guarda y actualiza el perfil")
    void perfil() throws SQLException {
        PerfilDAO dao = new PerfilDAO();
        Perfil p = new Perfil();
        p.setIdUsuario(idUsuario);
        p.setPesoKg(new BigDecimal("62.5"));
        p.setAlturaCm(new BigDecimal("165"));
        p.setObjetivo("Ser más activo");
        dao.guardar(p);
        p.setPesoKg(new BigDecimal("61.0"));
        dao.guardar(p);   // segunda vez actualiza, no duplica
        Perfil leido = dao.buscarPorUsuario(idUsuario);
        assertEquals(0, new BigDecimal("61.0").compareTo(leido.getPesoKg()));
        assertEquals("Ser más activo", leido.getObjetivo());
    }

    @Test @Order(4)
    @DisplayName("Registra agua y suma el total del día")
    void hidratacion() throws SQLException {
        HidratacionDAO dao = new HidratacionDAO();
        LocalDate hoy = LocalDate.now();
        dao.insertar(new Hidratacion(idUsuario, hoy, 250));
        dao.insertar(new Hidratacion(idUsuario, hoy, 500));
        assertEquals(750, dao.totalDelDia(idUsuario, hoy));
        assertTrue(dao.eliminarUltimo(idUsuario, hoy));
        assertEquals(250, dao.totalDelDia(idUsuario, hoy));
    }

    @Test @Order(5)
    @DisplayName("Registra actividad física y la lista por rango de fechas")
    void actividad() throws SQLException {
        List<Actividad> catalogo = new ActividadDAO().listar();
        assertEquals(8, catalogo.size());
        Actividad caminata = catalogo.stream().filter(a -> a.getNombre().equals("Caminata")).findFirst().orElseThrow();

        RegistroActividadDAO dao = new RegistroActividadDAO();
        RegistroActividad r = new RegistroActividad();
        r.setIdUsuario(idUsuario);
        r.setIdActividad(caminata.getIdActividad());
        r.setFecha(LocalDate.now());
        r.setDuracionMinutos(30);
        r.setCaloriasQuemadas(RegistroActividad.calcularCalorias(caminata.getCaloriasHora(), 30));
        dao.insertar(r);

        List<RegistroActividad> lista = dao.listarPorRango(idUsuario, LocalDate.now().minusDays(6), LocalDate.now());
        assertEquals(1, lista.size());
        assertEquals("Caminata", lista.get(0).getNombreActividad());
        assertEquals(0, new BigDecimal("140").compareTo(lista.get(0).getCaloriasQuemadas()));

        // Otro usuario no puede eliminar un registro ajeno
        assertFalse(dao.eliminar(lista.get(0).getIdRegistro(), idUsuario + 999));
        assertTrue(dao.eliminar(lista.get(0).getIdRegistro(), idUsuario));
    }
}
