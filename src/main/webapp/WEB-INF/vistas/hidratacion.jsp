<%-- Pantalla 5 - Seguimiento de hidratación (RF04). GET muestra el progreso; los formularios envían por POST. --%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="fragmentos/taglibs.jspf" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <title>Hidratación - VitaHealth</title>
  <%@ include file="fragmentos/cabecera.jspf" %>
</head>
<body>
<%@ include file="fragmentos/navegacion.jspf" %>
<main class="contenido">
  <div class="encabezado">
    <div><h1>Hidratación</h1><span class="texto-2">Meta diaria: ${metaVasos} vasos (${metaMl} ml)</span></div>
  </div>
  <%@ include file="fragmentos/mensajes.jspf" %>

  <div class="rejilla rejilla-2">
    <article class="tarjeta">
      <%-- Anillo de progreso: circunferencia = 2·π·52 ≈ 326.7 --%>
      <div class="anillo">
        <svg viewBox="0 0 120 120">
          <circle cx="60" cy="60" r="52" fill="none" stroke="#e7f2fc" stroke-width="10"/>
          <circle cx="60" cy="60" r="52" fill="none" stroke="#2f8fe0" stroke-width="10" stroke-linecap="round"
                  stroke-dasharray="326.7" stroke-dashoffset="${326.7 - 326.7 * porcentaje / 100}"/>
        </svg>
        <div class="centro">
          <svg width="26" height="26" viewBox="0 0 24 24" fill="#2f8fe0"><path d="M12 3s6 6.5 6 11a6 6 0 0 1-12 0c0-4.5 6-11 6-11z"/></svg>
          <strong>${vasos}</strong>
          <span class="texto-2">de ${metaVasos} vasos</span>
          <em>${porcentaje}%</em>
        </div>
      </div>

      <h3 style="text-align:center">Registro de hoy</h3>
      <div class="vasos" aria-label="${vasos} de ${metaVasos} vasos">
        <c:forEach begin="1" end="${metaVasos}" var="i">
          <span class="vaso ${i <= vasos ? 'lleno' : ''}"></span>
        </c:forEach>
      </div>

      <%-- Botones rápidos: cada uno es un formulario POST --%>
      <div class="botones-agua">
        <form action="${ctx}/hidratacion" method="post">
          <input type="hidden" name="accion" value="agregar"><input type="hidden" name="cantidad" value="250">
          <button class="boton azul bloque" type="submit">+ 1 vaso<br><small>250 ml</small></button>
        </form>
        <form action="${ctx}/hidratacion" method="post">
          <input type="hidden" name="accion" value="agregar"><input type="hidden" name="cantidad" value="500">
          <button class="boton azul bloque" type="submit">+ Botella<br><small>500 ml</small></button>
        </form>
        <form action="${ctx}/hidratacion" method="post">
          <input type="hidden" name="accion" value="deshacer">
          <button class="boton fantasma bloque" type="submit" ${empty registrosHoy ? 'disabled' : ''}>Deshacer<br><small>último</small></button>
        </form>
      </div>

      <form action="${ctx}/hidratacion" method="post" class="filtro" style="margin-top:1rem">
        <input type="hidden" name="accion" value="agregar">
        <div class="campo" style="flex:1">
          <label for="cantidad">Otra cantidad (ml)</label>
          <input type="number" id="cantidad" name="cantidad" min="50" max="3000" step="50" placeholder="Ej: 330" required>
        </div>
        <button class="boton" type="submit">Agregar</button>
      </form>

      <div class="consejo">
        <span style="font-size:1.8rem">💧</span>
        <div><strong>¡Sigue así!</strong><br><span class="pequeno texto-2">Recuerda beber agua durante todo el día.</span></div>
      </div>
    </article>

    <div class="rejilla">
      <article class="tarjeta">
        <div class="fila"><h2>Hoy</h2><span class="texto-2 pequeno">${totalMl} ml</span></div>
        <c:choose>
          <c:when test="${empty registrosHoy}"><p class="vacio">Aún no registras agua hoy.</p></c:when>
          <c:otherwise>
            <ul class="lista">
              <c:forEach var="h" items="${registrosHoy}">
                <li><div class="icono agua"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 3s6 6.5 6 11a6 6 0 0 1-12 0c0-4.5 6-11 6-11z"/></svg></div>
                  <div class="detalle"><strong>${h.cantidadMl} ml</strong><span class="texto-2 pequeno">Registro #${h.idHidratacion}</span></div></li>
              </c:forEach>
            </ul>
          </c:otherwise>
        </c:choose>
      </article>
      <article class="tarjeta">
        <h2>Semana (ml)</h2>
        <div class="grafico" style="height:140px">
          <c:forEach var="d" items="${semana}" varStatus="st">
            <div class="barra-dia ${st.last ? 'hoy' : ''}">
              <span class="valor">${d.aguaMl}</span>
              <div class="col agua" style="height:${d.aguaMl >= metaMl ? 100 : d.aguaMl * 100 / metaMl}%"></div>
              <span class="dia">${d.diaCorto}</span>
            </div>
          </c:forEach>
        </div>
      </article>
    </div>
  </div>
</main>
<jsp:include page="pie.jsp"><jsp:param name="modulo" value="Hidratación" /></jsp:include>
</body>
</html>
