<%-- Pantalla 4 - Panel principal (RF04, RF05, RF06). Datos enviados por DashboardServlet (GET). --%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="fragmentos/taglibs.jspf" %>
<jsp:useBean id="ahora" class="java.util.Date" />
<!DOCTYPE html>
<html lang="es">
<head>
  <title>Inicio - VitaHealth</title>
  <%@ include file="fragmentos/cabecera.jspf" %>
</head>
<body>
<%@ include file="fragmentos/navegacion.jspf" %>
<main class="contenido">
  <div class="encabezado">
    <div>
      <h1>¡Hola, <c:out value="${usuario.nombre}" />! 👋</h1>
      <span class="fecha"><fmt:formatDate value="${ahora}" pattern="EEEE d 'de' MMMM 'de' yyyy" /></span>
    </div>
    <%-- Acceso rápido: formulario POST que registra un vaso de agua desde el panel --%>
    <form action="${ctx}/hidratacion" method="post">
      <input type="hidden" name="accion" value="agregar">
      <input type="hidden" name="cantidad" value="250">
      <button class="boton azul" type="submit">+ 1 vaso de agua</button>
    </form>
  </div>

  <%@ include file="fragmentos/mensajes.jspf" %>

  <h2 style="margin-bottom:.8rem">Resumen de hoy</h2>
  <div class="rejilla rejilla-3">
    <article class="tarjeta">
      <div class="tarjeta-titulo">
        <div class="icono agua"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 3s6 6.5 6 11a6 6 0 0 1-12 0c0-4.5 6-11 6-11z"/></svg></div>
        <h3>Hidratación</h3>
      </div>
      <div class="fila">
        <span class="cifra">${vasosHoy} <small>de 8 vasos</small></span>
        <strong style="color:var(--azul)">${porcentajeAgua}%</strong>
      </div>
      <div class="progreso agua"><span style="width:${porcentajeAgua}%"></span></div>
      <p class="pequeno texto-2" style="margin-top:.5rem">${aguaHoy} ml de ${metaAgua} ml · <a href="${ctx}/hidratacion">Registrar</a></p>
    </article>

    <article class="tarjeta">
      <div class="tarjeta-titulo">
        <div class="icono act"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="14" cy="4" r="2"/><path d="M7 21l3-6 3 2v5M10 15l1.5-6 3.5 3h3M11.5 9 8 10l-2 3"/></svg></div>
        <h3>Actividad física</h3>
      </div>
      <div class="fila">
        <span class="cifra">${hoy.minutos} <small>min</small></span>
        <strong class="kcal"><fmt:formatNumber value="${hoy.calorias}" maxFractionDigits="0" /> kcal</strong>
      </div>
      <div class="progreso"><span style="width:${porcentajeMinutos}%"></span></div>
      <p class="pequeno texto-2" style="margin-top:.5rem">Meta diaria: ${metaMinutos} min · <a href="${ctx}/actividad">Registrar</a></p>
    </article>

    <article class="tarjeta">
      <div class="tarjeta-titulo">
        <div class="icono imc"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><rect x="3" y="4" width="18" height="16" rx="4"/><path d="M8 10a4 4 0 0 1 8 0M12 10l2-2"/></svg></div>
        <h3>Índice de masa corporal</h3>
      </div>
      <c:choose>
        <c:when test="${not empty perfil and not empty perfil.imc}">
          <div class="fila">
            <span class="cifra">${perfil.imc}</span>
            <span class="etiqueta">${perfil.clasificacionImc}</span>
          </div>
          <p class="pequeno texto-2" style="margin-top:.5rem"><fmt:formatNumber value="${perfil.pesoKg}" maxFractionDigits="1" /> kg · <fmt:formatNumber value="${perfil.alturaCm}" maxFractionDigits="1" /> cm · <a href="${ctx}/perfil">Actualizar</a></p>
        </c:when>
        <c:otherwise>
          <p class="texto-2">Completa tu peso y altura para calcular tu IMC.</p>
          <a class="boton secundario pequeno" href="${ctx}/perfil">Completar perfil</a>
        </c:otherwise>
      </c:choose>
    </article>
  </div>

  <div class="rejilla rejilla-2" style="margin-top:1rem">
    <article class="tarjeta">
      <div class="fila"><h2>Actividad de la semana (minutos)</h2><span class="texto-2 pequeno">Últimos 7 días</span></div>
      <div class="grafico">
        <c:forEach var="d" items="${semana}" varStatus="st">
          <div class="barra-dia ${st.last ? 'hoy' : ''}">
            <span class="valor">${d.minutos}</span>
            <div class="col" style="height:${d.minutos * 100 / maxMinutos}%"></div>
            <span class="dia">${d.diaCorto}</span>
          </div>
        </c:forEach>
      </div>
    </article>
    <article class="tarjeta">
      <h2>Estadísticas</h2>
      <ul class="lista">
        <li><div class="icono act"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg></div>
          <div class="detalle"><strong>${minutosSemana} min</strong><span class="texto-2 pequeno">Actividad en 7 días (meta OMS: 150)</span></div></li>
        <li><div class="icono fuego"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2c1 4 6 6 6 12a6 6 0 0 1-12 0c0-3 1.5-5 3-6.5 0 2 1 3.5 2.5 3.5C11.5 8 11 5 12 2z"/></svg></div>
          <div class="detalle"><strong><fmt:formatNumber value="${caloriasSemana}" maxFractionDigits="0" /> kcal</strong><span class="texto-2 pequeno">Calorías quemadas en 7 días</span></div></li>
        <li><div class="icono agua"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 3s6 6.5 6 11a6 6 0 0 1-12 0c0-4.5 6-11 6-11z"/></svg></div>
          <div class="detalle"><strong><c:out value="${empty perfil.objetivo ? 'Sin objetivo definido' : perfil.objetivo}" /></strong><span class="texto-2 pequeno">Tu objetivo personal</span></div></li>
      </ul>
    </article>
  </div>
</main>
<jsp:include page="pie.jsp"><jsp:param name="modulo" value="Panel principal" /></jsp:include>
</body>
</html>
