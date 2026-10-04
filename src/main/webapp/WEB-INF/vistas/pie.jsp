<%-- Pie de página. Se incluye dinámicamente con <jsp:include> y recibe un parámetro con <jsp:param> --%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.Year" %>
<footer class="pie">
  &copy; <%= Year.now().getValue() %> VitaHealth &middot; Grupo 5 &middot; SENA ADSO
  &middot; Módulo: ${empty param.modulo ? 'General' : param.modulo}
</footer>
