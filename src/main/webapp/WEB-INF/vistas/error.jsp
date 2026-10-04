<%-- Página de error (404 / 500). isErrorPage permite usar el objeto implícito "exception". --%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ include file="fragmentos/taglibs.jspf" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <title>Ups - VitaHealth</title>
  <%@ include file="fragmentos/cabecera.jspf" %>
</head>
<body>
<main class="bienvenida">
  <div class="tarjeta" style="max-width:440px;text-align:center">
    <img src="${ctx}/img/logo.svg" alt="" style="width:64px">
    <c:choose>
      <c:when test="${pageContext.errorData.statusCode == 404}">
        <h1>Página no encontrada</h1>
        <p class="texto-2">La dirección que buscas no existe.</p>
      </c:when>
      <c:otherwise>
        <h1>Algo salió mal</h1>
        <p class="texto-2">Ocurrió un error procesando tu solicitud. Intenta de nuevo.</p>
        <% if (exception != null) { application.log("Error VitaHealth", exception); } %>
      </c:otherwise>
    </c:choose>
    <a class="boton" href="${ctx}/">Volver al inicio</a>
  </div>
</main>
</body>
</html>
