<%-- Pantalla 8 - Perfil del usuario (RF03, RF08). GET carga los datos; el formulario envía por POST. --%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="fragmentos/taglibs.jspf" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <title>Mi perfil - VitaHealth</title>
  <%@ include file="fragmentos/cabecera.jspf" %>
</head>
<body>
<%@ include file="fragmentos/navegacion.jspf" %>
<main class="contenido">
  <div class="encabezado"><h1>Mi perfil</h1></div>
  <%@ include file="fragmentos/mensajes.jspf" %>

  <div class="rejilla rejilla-2">
    <article class="tarjeta">
      <div class="perfil-cabecera">
        <div class="avatar grande">${fn:escapeXml(usuario.inicial)}</div>
        <div>
          <h2><c:out value="${usuario.nombreCompleto}" /></h2>
          <span class="texto-2"><c:out value="${usuario.correo}" /></span><br>
          <span class="etiqueta">${usuario.rol}</span>
        </div>
      </div>

      <c:if test="${not empty errores}"><div class="alerta error">Revisa los campos marcados.</div></c:if>

      <form action="${ctx}/perfil" method="post" novalidate>
        <h3>Información personal</h3>
        <div class="dos-campos">
          <div class="campo ${not empty errores.nombre ? 'con-error' : ''}">
            <label for="nombre">Nombre</label>
            <input id="nombre" name="nombre" maxlength="80" value="${fn:escapeXml(empty param.nombre ? usuario.nombre : param.nombre)}" required>
            <c:if test="${not empty errores.nombre}"><div class="error-campo">${errores.nombre}</div></c:if>
          </div>
          <div class="campo ${not empty errores.apellido ? 'con-error' : ''}">
            <label for="apellido">Apellido</label>
            <input id="apellido" name="apellido" maxlength="80" value="${fn:escapeXml(empty param.apellido ? usuario.apellido : param.apellido)}" required>
            <c:if test="${not empty errores.apellido}"><div class="error-campo">${errores.apellido}</div></c:if>
          </div>
        </div>
        <div class="campo ${not empty errores.fechaNacimiento ? 'con-error' : ''}">
          <label for="fechaNacimiento">Fecha de nacimiento</label>
          <input type="date" id="fechaNacimiento" name="fechaNacimiento" value="${perfil.fechaNacimiento}">
          <c:if test="${not empty errores.fechaNacimiento}"><div class="error-campo">${errores.fechaNacimiento}</div></c:if>
        </div>
        <h3>Mis datos físicos y objetivo</h3>
        <div class="dos-campos">
          <div class="campo ${not empty errores.peso ? 'con-error' : ''}">
            <label for="peso">Peso (kg)</label>
            <input type="number" id="peso" name="peso" step="0.1" min="20" max="400" value="${perfil.pesoKg}" placeholder="62.5">
            <c:if test="${not empty errores.peso}"><div class="error-campo">${errores.peso}</div></c:if>
          </div>
          <div class="campo ${not empty errores.altura ? 'con-error' : ''}">
            <label for="altura">Altura (cm)</label>
            <input type="number" id="altura" name="altura" step="0.1" min="50" max="250" value="${perfil.alturaCm}" placeholder="165">
            <c:if test="${not empty errores.altura}"><div class="error-campo">${errores.altura}</div></c:if>
          </div>
        </div>
        <div class="campo">
          <label for="objetivo">Objetivo</label>
          <select id="objetivo" name="objetivo">
            <c:forEach var="o" items="${['Mantener mi peso','Bajar de peso','Ganar masa muscular','Mejorar mi hidratación','Ser más activo']}">
              <option ${perfil.objetivo == o ? 'selected' : ''}>${o}</option>
            </c:forEach>
          </select>
        </div>
        <button class="boton" type="submit">Guardar cambios</button>
      </form>
    </article>

    <div class="rejilla">
      <article class="tarjeta">
        <div class="tarjeta-titulo">
          <div class="icono imc"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><rect x="3" y="4" width="18" height="16" rx="4"/><path d="M8 10a4 4 0 0 1 8 0M12 10l2-2"/></svg></div>
          <h3>Índice de masa corporal (IMC)</h3>
        </div>
        <div class="dato-imc">
          <div class="cifra">${empty perfil.imc ? '--' : perfil.imc}</div>
          <span class="etiqueta">${perfil.clasificacionImc}</span>
        </div>
        <p class="texto-2 pequeno">IMC = peso (kg) / altura (m)². Rango saludable según la OMS: 18,5 a 24,9.</p>
      </article>
      <article class="tarjeta">
        <h3>Datos de la cuenta</h3>
        <ul class="lista">
          <li><div class="detalle"><strong>Edad</strong><span class="texto-2 pequeno">${empty perfil.edad ? 'Sin fecha de nacimiento' : perfil.edad += ' años'}</span></div></li>
          <li><div class="detalle"><strong>Miembro desde</strong><span class="texto-2 pequeno">${fn:substring(usuario.fechaRegistro, 0, 10)}</span></div></li>
          <li><div class="detalle"><strong>Objetivo</strong><span class="texto-2 pequeno"><c:out value="${empty perfil.objetivo ? 'Sin definir' : perfil.objetivo}" /></span></div></li>
        </ul>
      </article>
    </div>
  </div>
</main>
<jsp:include page="pie.jsp"><jsp:param name="modulo" value="Perfil" /></jsp:include>
</body>
</html>
