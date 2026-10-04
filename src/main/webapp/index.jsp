<%--
  Pantalla 1 - Bienvenida (pública).
  Elementos JSP usados: directivas (page, include), declaración <%! %>, scriptlet <% %>,
  expresión <%= %>, lenguaje de expresiones ${} y JSTL.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.LocalTime" %>
<%@ include file="/WEB-INF/vistas/fragmentos/taglibs.jspf" %>
<%!
    // Declaración JSP: método disponible en toda la página
    private String saludoSegunHora(int hora) {
        if (hora < 12) return "¡Buenos días!";
        if (hora < 19) return "¡Buenas tardes!";
        return "¡Buenas noches!";
    }
%>
<%
    // Scriptlet: si ya hay sesión iniciada, se envía directo al panel
    if (session.getAttribute("usuario") != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }
    int hora = LocalTime.now().getHour();
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <title>VitaHealth - Bienvenida</title>
  <%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>
</head>
<body>
<main class="bienvenida">
  <div class="bienvenida-caja">
    <section>
      <div class="marca" style="margin-bottom:1.2rem">
        <img src="${ctx}/img/logo.svg" alt="" style="width:56px;height:56px">
        <span><strong style="font-size:1.6rem">VitaHealth</strong><small>Tu estilo de vida saludable</small></span>
      </div>
      <p class="texto-2" style="font-weight:700"><%= saludoSegunHora(hora) %></p>
      <h1>Bienvenido a VitaHealth</h1>
      <p class="lema">Tu compañera para una vida más saludable y equilibrada.</p>
      <ul class="lista-beneficios">
        <li><span style="background:var(--azul-suave);color:var(--azul)">💧</span>Controla tu consumo diario de agua</li>
        <li><span style="background:var(--verde-suave);color:var(--verde)">🏃</span>Registra tu actividad física y calorías</li>
        <li><span style="background:#f1eefc;color:var(--morado)">📊</span>Mira tu progreso de la semana</li>
      </ul>
      <div class="acciones">
        <a class="boton" href="${ctx}/login">Comenzar</a>
        <a class="boton secundario" href="${ctx}/registro">Crear cuenta</a>
      </div>
    </section>
    <section class="ilustracion" aria-hidden="true">
      <svg viewBox="0 0 360 300" width="100%">
        <circle cx="180" cy="160" r="120" fill="#e8f6ed"/>
        <path d="M180 250c-40-28-80-56-80-102 0-28 22-48 48-48 14 0 26 7 32 17 6-10 18-17 32-17 26 0 48 20 48 48 0 46-40 74-80 102z" fill="#1a9b4b"/>
        <circle cx="183" cy="128" r="12" fill="#fff"/>
        <path d="M158 148c11 6 22 7 33 3l14-17 8 6-17 22-5 28 17 20-8 6-20-20-11 17-8-3 14-31v-17c-8 0-17-3-23-8z" fill="#fff"/>
        <g transform="translate(62 70)"><circle r="26" fill="#fff"/><path d="M0-14s11 12 11 20a11 11 0 0 1-22 0c0-8 11-20 11-20z" fill="#2f8fe0"/></g>
        <g transform="translate(300 96)"><circle r="26" fill="#fff"/><path d="M-12 0h24M-12-6v12M12-6v12" stroke="#7c6ad8" stroke-width="5" stroke-linecap="round"/></g>
        <g transform="translate(290 228)"><circle r="22" fill="#fff"/><path d="M-11 0h6l3-8 5 15 3-7h6" fill="none" stroke="#e04848" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/></g>
        <path d="M60 236c10-20 30-26 40-20-6 14-22 24-40 20z" fill="#8fd19e"/>
      </svg>
    </section>
  </div>
</main>
</body>
</html>
