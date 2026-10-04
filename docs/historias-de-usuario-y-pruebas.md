# Historias de usuario, trazabilidad y plan de pruebas

Módulo web VitaHealth · Evidencia GA7-220501096-AA2-EV02

## 1. Historias de usuario implementadas

Las historias salen de los requisitos funcionales definidos en las evidencias GA4-220501095-AA2-EV02 (informe de entregables) y GA5-220501095-AA1-EV03 (interfaz gráfica y mapa de navegación).

| ID | Historia de usuario | Criterios de aceptación | RF | Componentes |
|---|---|---|---|---|
| HU01 | **Como** persona nueva **quiero** crear una cuenta con mi nombre, correo y contraseña **para** usar VitaHealth. | Todos los campos son obligatorios. El correo es válido y no está registrado. La contraseña tiene 8 o más caracteres, con letras y números, y coincide con la confirmación. Hay que aceptar los términos. | RF01 | `RegistroServlet`, `registro.jsp`, `UsuarioDAO`, `PasswordUtil` |
| HU02 | **Como** usuario registrado **quiero** iniciar sesión con correo y contraseña **para** ver mi información de forma segura. | Si los datos son incorrectos se muestra un error genérico. Con datos correctos se crea la sesión y se abre el panel. Se puede recordar el correo con una cookie. Las páginas internas exigen sesión. | RF02 | `LoginServlet`, `login.jsp`, `AutenticacionFiltro` |
| HU03 | **Como** usuario **quiero** ver un resumen de mi día y de mi semana **para** saber cómo voy con mis metas. | El panel muestra los vasos de agua y el % de la meta, los minutos y kcal de hoy, el IMC y un gráfico de minutos de los últimos 7 días. | RF06 | `DashboardServlet`, `EstadisticasServicio`, `dashboard.jsp` |
| HU04 | **Como** usuario **quiero** registrar el agua que tomo **para** cumplir la meta de 8 vasos al día. | Hay botones de +250 ml y +500 ml y un campo de cantidad libre (50 a 3000 ml). Se puede deshacer el último registro. Se ven el anillo de progreso, los vasos llenos y el total de la semana. | RF04 | `HidratacionServlet`, `hidratacion.jsp`, `HidratacionDAO` |
| HU05 | **Como** usuario **quiero** registrar mis actividades físicas **para** llevar control de minutos y calorías. | Se elige la actividad del catálogo, la duración (1 a 600 min) y la fecha (no futura). Las calorías se calculan solas. Se puede eliminar un registro. El historial se filtra por fechas. | RF05 | `ActividadServlet`, `actividad.jsp`, `RegistroActividadDAO`, `ActividadDAO` |
| HU06 | **Como** usuario **quiero** registrar y editar mi peso, altura, fecha de nacimiento y objetivo **para** conocer mi IMC. | Los rangos se validan (peso de 20 a 400 kg, altura de 50 a 250 cm, edad mínima de 13 años). Se muestra el IMC con su clasificación de la OMS. | RF03, RF08 | `PerfilServlet`, `perfil.jsp`, `PerfilDAO` |
| HU07 | **Como** usuario **quiero** cerrar sesión **para** proteger mi cuenta en equipos compartidos. | Aparece un modal de confirmación. La sesión se invalida y vuelve al inicio de sesión. | RNF04 | `LogoutServlet`, `navegacion.jspf` |

## 2. Diagrama de clases implementado (resumen)

```
          «abstract» Persona
          - nombre, apellido, correo
          + getNombreCompleto()
          + getRol() «abstract»
                 ▲
                 │ herencia
              Usuario ───1────────0..1── Perfil (pesoKg, alturaCm, objetivo, getImc())
          - idUsuario, contrasena   │
          - fechaRegistro, estado   ├──1────────*── Hidratacion (fecha, cantidadMl)
                                    │
                                    └──1────────*── RegistroActividad (fecha, duracion, calorias)
                                                          *
                                                          │
                                                          1
                                                      Actividad (nombre, tipo, caloriasHora)
```

## 3. Plan y resultados de pruebas funcionales

Entorno: Windows, JDK 21, Apache Tomcat 10.1.34, navegador web, base H2 (modo PostgreSQL).

| Caso | Historia | Pasos | Resultado esperado | Método HTTP |
|---|---|---|---|---|
| CP01 | — | Abrir `http://localhost:8080/vitahealth/` | Se ve la pantalla de bienvenida con saludo según la hora | GET |
| CP02 | HU01 | Enviar el registro con campos vacíos o contraseña débil | Errores bajo cada campo y nada se guarda | POST |
| CP03 | HU01 | Registrar un usuario válido | Redirige al login con el mensaje "¡Cuenta creada!" | POST → GET |
| CP04 | HU02 | Iniciar sesión con contraseña incorrecta | Mensaje "Correo o contraseña incorrectos" | POST |
| CP05 | HU02 | Escribir `/vitahealth/dashboard` sin haber iniciado sesión | Redirige al login con "Debes iniciar sesión" | GET |
| CP06 | HU02, HU03 | Iniciar sesión con datos correctos | Abre el panel con "¡Hola, nombre!" | POST → GET |
| CP07 | HU04 | Pulsar "+ 1 vaso" varias veces y registrar otra cantidad | Suben el anillo, los vasos y el % y aparece un mensaje de confirmación | POST |
| CP08 | HU05 | Registrar "Caminata, 30 min" | Aparece en el historial con 140 kcal | POST |
| CP09 | HU05 | Filtrar el historial con fechas | La URL muestra `?desde=…&hasta=…` y la lista se filtra | GET |
| CP10 | HU06 | Guardar el perfil con peso 62,5 kg y altura 165 cm | IMC 23,0, "Peso saludable" | POST |
| CP11 | HU03 | Volver al panel | Las tarjetas y el gráfico semanal reflejan lo registrado | GET |
| CP12 | HU07 | Pulsar "Salir" y confirmar | Vuelve al login con "Cerraste sesión correctamente" | POST |

## 4. Pruebas automatizadas (JUnit 5)

| Clase | Pruebas | Qué verifica |
|---|---|---|
| `PasswordUtilTest` | 3 | El hash no guarda texto plano, la verificación es correcta y la sal es aleatoria |
| `ValidadorTest` | 4 | Correos, contraseña segura, conversión de números y fechas, limpieza de texto |
| `CalculosSaludTest` | 5 | IMC, calorías, vasos y % de hidratación, herencia `Persona`→`Usuario` |
| `DaoIntegracionTest` | 5 | Inserción y búsqueda de usuario, correo único, perfil, hidratación, actividad y que no se puedan borrar registros ajenos |
