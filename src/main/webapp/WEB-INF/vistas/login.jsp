<%-- Pantalla 2 - Inicio de sesión (RF02). Formulario HTML que envía por POST a LoginServlet. --%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="fragmentos/taglibs.jspf" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <title>Iniciar sesión - VitaHealth</title>
  <%@ include file="fragmentos/cabecera.jspf" %>
</head>
<body>
<main class="acceso">
  <section class="acceso-arte">
    <img class="logo-grande" src="${ctx}/img/logo.svg" alt="">
    <h1>VitaHealth</h1>
    <p>Tu bienestar, nuestro propósito. Registra tus hábitos y mira tu progreso cada día.</p>
    <div class="burbujas" aria-hidden="true">
      <div class="burbuja"><svg viewBox="0 0 24 24" fill="#2f8fe0"><path d="M12 3s6 6.5 6 11a6 6 0 0 1-12 0c0-4.5 6-11 6-11z"/></svg></div>
      <div class="burbuja"><svg viewBox="0 0 24 24" fill="none" stroke="#1a9b4b" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="14" cy="4" r="2"/><path d="M7 21l3-6 3 2v5M10 15l1.5-6 3.5 3h3M11.5 9 8 10l-2 3"/></svg></div>
      <div class="burbuja"><svg viewBox="0 0 24 24" fill="#e04848"><path d="M12 21C6 17 2 13.5 2 8.5 2 5.5 4.5 3 7.5 3c1.9 0 3.5 1 4.5 2.4C13 4 14.6 3 16.5 3 19.5 3 22 5.5 22 8.5 22 13.5 18 17 12 21z"/></svg></div>
    </div>
  </section>

  <section class="acceso-form">
    <div class="acceso-caja">
      <h2>¡Bienvenido!</h2>
      <p class="sub">Inicia sesión para continuar</p>

      <%-- Mensajes según parámetros que llegan por GET en la URL --%>
      <c:if test="${param.registrado == '1'}"><div class="alerta exito">¡Cuenta creada! Ya puedes iniciar sesión.</div></c:if>
      <c:if test="${param.salida == '1'}"><div class="alerta info">Cerraste sesión correctamente.</div></c:if>
      <c:if test="${param.requiere == '1'}"><div class="alerta info">Debes iniciar sesión para ver esa página.</div></c:if>
      <c:if test="${not empty error}"><div class="alerta error"><c:out value="${error}" /></div></c:if>

      <form action="${ctx}/login" method="post" novalidate>
        <div class="campo">
          <label for="correo">Correo electrónico</label>
          <input type="email" id="correo" name="correo" placeholder="ejemplo@correo.com"
                 value="${fn:escapeXml(correo)}" required autocomplete="email">
        </div>
        <div class="campo">
          <label for="contrasena">Contraseña</label>
          <input type="password" id="contrasena" name="contrasena" placeholder="••••••••" required autocomplete="current-password">
        </div>
        <label class="check"><input type="checkbox" name="recordar" ${not empty correo ? 'checked' : ''}> Recordar mi correo en este equipo</label>
        <button type="submit" class="boton bloque">Iniciar sesión</button>
      </form>
      <p class="separador">¿No tienes cuenta? <a href="${ctx}/registro">Regístrate</a></p>
      <p class="separador"><a href="${ctx}/" class="texto-2">← Volver al inicio</a></p>
    </div>
  </section>
</main>
</body>
</html>
