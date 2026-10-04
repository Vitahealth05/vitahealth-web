<%-- Pantalla 3 - Crear cuenta (RF01). Formulario HTML que envía por POST a RegistroServlet. --%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="fragmentos/taglibs.jspf" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <title>Crear cuenta - VitaHealth</title>
  <%@ include file="fragmentos/cabecera.jspf" %>
</head>
<body>
<main class="acceso">
  <section class="acceso-arte">
    <img class="logo-grande" src="${ctx}/img/logo.svg" alt="">
    <h1>Únete a VitaHealth</h1>
    <p>Crea tu cuenta gratis y empieza a construir hábitos saludables hoy.</p>
  </section>

  <section class="acceso-form">
    <div class="acceso-caja">
      <h2>Crear cuenta</h2>
      <p class="sub">Completa tus datos para registrarte</p>

      <c:if test="${not empty errores}">
        <div class="alerta error">Revisa los campos marcados.</div>
      </c:if>

      <form action="${ctx}/registro" method="post" novalidate>
        <div class="dos-campos">
          <div class="campo ${not empty errores.nombre ? 'con-error' : ''}">
            <label for="nombre">Nombre</label>
            <input id="nombre" name="nombre" maxlength="80" value="${fn:escapeXml(nombre)}" placeholder="Laura" required>
            <c:if test="${not empty errores.nombre}"><div class="error-campo">${errores.nombre}</div></c:if>
          </div>
          <div class="campo ${not empty errores.apellido ? 'con-error' : ''}">
            <label for="apellido">Apellido</label>
            <input id="apellido" name="apellido" maxlength="80" value="${fn:escapeXml(apellido)}" placeholder="Gómez" required>
            <c:if test="${not empty errores.apellido}"><div class="error-campo">${errores.apellido}</div></c:if>
          </div>
        </div>
        <div class="campo ${not empty errores.correo ? 'con-error' : ''}">
          <label for="correo">Correo electrónico</label>
          <input type="email" id="correo" name="correo" maxlength="120" value="${fn:escapeXml(correo)}" placeholder="ejemplo@correo.com" required>
          <c:if test="${not empty errores.correo}"><div class="error-campo">${errores.correo}</div></c:if>
        </div>
        <div class="campo ${not empty errores.contrasena ? 'con-error' : ''}">
          <label for="contrasena">Contraseña</label>
          <input type="password" id="contrasena" name="contrasena" placeholder="Mínimo 8 caracteres, letras y números" required autocomplete="new-password">
          <c:if test="${not empty errores.contrasena}"><div class="error-campo">${errores.contrasena}</div></c:if>
        </div>
        <div class="campo ${not empty errores.confirmar ? 'con-error' : ''}">
          <label for="confirmar">Confirmar contraseña</label>
          <input type="password" id="confirmar" name="confirmar" required autocomplete="new-password">
          <c:if test="${not empty errores.confirmar}"><div class="error-campo">${errores.confirmar}</div></c:if>
        </div>
        <label class="check">
          <input type="checkbox" name="terminos">
          <span>Acepto los <a href="#">Términos y Condiciones</a> y la <a href="#">Política de Privacidad</a> (Ley 1581 de 2012).</span>
        </label>
        <c:if test="${not empty errores.terminos}"><div class="error-campo" style="margin:-.6rem 0 .8rem">${errores.terminos}</div></c:if>
        <button type="submit" class="boton bloque">Registrarme</button>
      </form>
      <p class="separador">¿Ya tienes cuenta? <a href="${ctx}/login">Inicia sesión</a></p>
    </div>
  </section>
</main>
</body>
</html>
