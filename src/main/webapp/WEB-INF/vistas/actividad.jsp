<%-- Pantalla 6 - Registro de actividad física (RF05).
     Formulario de registro: method="post". Filtro del historial: method="get" (parámetros en la URL). --%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="fragmentos/taglibs.jspf" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <title>Actividad física - VitaHealth</title>
  <%@ include file="fragmentos/cabecera.jspf" %>
</head>
<body>
<%@ include file="fragmentos/navegacion.jspf" %>
<main class="contenido">
  <div class="encabezado">
    <div><h1>Actividad física</h1><span class="texto-2">Registra tus ejercicios y revisa tu historial</span></div>
  </div>
  <%@ include file="fragmentos/mensajes.jspf" %>

  <div class="rejilla rejilla-2">
    <article class="tarjeta">
      <div class="fila" style="margin-bottom:.8rem">
        <h2>Historial</h2>
        <span class="texto-2 pequeno">${totalMinutos} min · <span class="kcal"><fmt:formatNumber value="${totalCalorias}" maxFractionDigits="0" /> kcal</span></span>
      </div>

      <%-- Formulario GET: filtra el historial por rango de fechas --%>
      <form action="${ctx}/actividad" method="get" class="filtro" style="margin-bottom:.6rem">
        <div class="campo"><label for="desde">Desde</label><input type="date" id="desde" name="desde" value="${desde}" max="${hoy}"></div>
        <div class="campo"><label for="hasta">Hasta</label><input type="date" id="hasta" name="hasta" value="${hasta}" max="${hoy}"></div>
        <button class="boton secundario" type="submit">Filtrar</button>
      </form>

      <c:choose>
        <c:when test="${empty registros}">
          <p class="vacio">No hay actividades entre ${desde} y ${hasta}.<br>¡Registra la primera!</p>
        </c:when>
        <c:otherwise>
          <ul class="lista">
            <c:forEach var="r" items="${registros}">
              <li>
                <div class="icono act"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="14" cy="4" r="2"/><path d="M7 21l3-6 3 2v5M10 15l1.5-6 3.5 3h3M11.5 9 8 10l-2 3"/></svg></div>
                <div class="detalle">
                  <strong><c:out value="${r.nombreActividad}" /> <span class="etiqueta ${r.tipoActividad}">${r.tipoActividad}</span></strong>
                  <span class="texto-2 pequeno">${r.fechaTexto} · ${r.duracionMinutos} min</span>
                </div>
                <span class="kcal"><fmt:formatNumber value="${r.caloriasQuemadas}" maxFractionDigits="0" /> kcal</span>
                <form action="${ctx}/actividad" method="post">
                  <input type="hidden" name="accion" value="eliminar">
                  <input type="hidden" name="idRegistro" value="${r.idRegistro}">
                  <button class="boton peligro" type="submit" title="Eliminar">✕</button>
                </form>
              </li>
            </c:forEach>
          </ul>
        </c:otherwise>
      </c:choose>
    </article>

    <article class="tarjeta">
      <h2>+ Agregar actividad</h2>
      <p class="texto-2 pequeno">Las calorías se calculan según la actividad y la duración.</p>
      <%-- Formulario POST: registra una nueva actividad --%>
      <form action="${ctx}/actividad" method="post">
        <input type="hidden" name="accion" value="registrar">
        <div class="campo">
          <label for="idActividad">Actividad</label>
          <select id="idActividad" name="idActividad" required>
            <option value="">Selecciona...</option>
            <c:forEach var="a" items="${catalogo}">
              <option value="${a.idActividad}">${a.nombre} (${a.tipo} · <fmt:formatNumber value="${a.caloriasHora}" maxFractionDigits="0" /> kcal/h)</option>
            </c:forEach>
          </select>
        </div>
        <div class="dos-campos">
          <div class="campo">
            <label for="duracion">Duración (min)</label>
            <input type="number" id="duracion" name="duracion" min="1" max="600" placeholder="30" required>
          </div>
          <div class="campo">
            <label for="fecha">Fecha</label>
            <input type="date" id="fecha" name="fecha" value="${hoy}" max="${hoy}" required>
          </div>
        </div>
        <button class="boton bloque" type="submit">Guardar actividad</button>
      </form>
    </article>
  </div>
</main>
<jsp:include page="pie.jsp"><jsp:param name="modulo" value="Actividad física" /></jsp:include>
</body>
</html>
