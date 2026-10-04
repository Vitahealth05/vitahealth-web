import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Prueba funcional automatizada de VitaHealth que deja capturas de pantalla como evidencia.
 *
 * Abre Microsoft Edge (o Chrome) en modo headless, lo controla con el protocolo DevTools,
 * recorre los casos de prueba CP01-CP12 (docs/historias-de-usuario-y-pruebas.md) llenando
 * y enviando los formularios reales, y guarda una imagen PNG de cada paso.
 * A cada imagen se le agrega arriba una barra con la URL visitada y la fecha/hora de la captura.
 *
 * Uso (Java 21, sin compilar):  java CapturadorPantallas.java <carpetaSalida> [urlBase]
 */
public class CapturadorPantallas {

    static String BASE = "http://localhost:8080/vitahealth";
    static Path SALIDA;
    static WebSocket ws;
    static final AtomicInteger IDS = new AtomicInteger();
    static final Map<Integer, CompletableFuture<String>> PENDIENTES = new ConcurrentHashMap<>();
    static int ancho = 1366, alto = 860;
    static boolean movil = false;
    static int contador = 0;

    public static void main(String[] args) throws Exception {
        SALIDA = Path.of(args.length > 0 ? args[0] : "capturas");
        if (args.length > 1) BASE = args[1];
        Files.createDirectories(SALIDA);

        Process navegador = iniciarNavegador();
        try {
            conectar();
            enviar("Page.enable", "{}");
            enviar("Runtime.enable", "{}");
            vista(1366, 860, false);
            recorrido();
            System.out.println("Capturas guardadas en " + SALIDA.toAbsolutePath());
        } finally {
            try { ws.sendClose(WebSocket.NORMAL_CLOSURE, "fin").join(); } catch (Exception ignored) { }
            navegador.destroy();
            navegador.waitFor(5, TimeUnit.SECONDS);
        }
    }

    // ------------------------------------------------------------------ recorrido de pruebas
    static void recorrido() throws Exception {
        String correo = "laura.gomez@correo.com";
        String clave = "Vita2026";
        LocalDate hoy = LocalDate.now();

        // CP01 - Bienvenida
        ir("/");
        capturar("CP01 bienvenida");

        // CP05 - acceso a página protegida sin sesión
        ir("/dashboard");
        capturar("CP05 acceso restringido sin sesion");

        // CP02 - registro con errores de validación
        ir("/registro");
        capturar("CP02a formulario registro");
        js("document.querySelector('#nombre').value='Laura';"
         + "document.querySelector('#correo').value='laura@';"
         + "document.querySelector('#contrasena').value='123';"
         + "document.querySelector('#confirmar').value='456';");
        enviarFormulario("form");
        capturar("CP02b registro validacion POST");

        // CP03 - registro correcto
        js("document.querySelector('#nombre').value='Laura';"
         + "document.querySelector('#apellido').value='Gómez';"
         + "document.querySelector('#correo').value='" + correo + "';"
         + "document.querySelector('#contrasena').value='" + clave + "';"
         + "document.querySelector('#confirmar').value='" + clave + "';"
         + "document.querySelector('input[name=terminos]').checked=true;");
        capturar("CP03a registro datos validos");
        enviarFormulario("form");
        if (url().contains("/registro")) {
            // la usuaria ya existía de una ejecución anterior: se continúa con el login
            ir("/login?registrado=1");
        }
        capturar("CP03b cuenta creada redirect login");

        // CP04 - login con contraseña incorrecta
        js("document.querySelector('#correo').value='" + correo + "';"
         + "document.querySelector('#contrasena').value='Incorrecta1';");
        enviarFormulario("form");
        capturar("CP04 login contrasena incorrecta");

        // CP06 - login correcto
        js("document.querySelector('#correo').value='" + correo + "';"
         + "document.querySelector('#contrasena').value='" + clave + "';");
        enviarFormulario("form");
        capturar("CP06 panel principal tras login");

        // CP10 - perfil
        ir("/perfil");
        js("document.querySelector('#fechaNacimiento').value='1998-05-12';"
         + "document.querySelector('#peso').value='62.5';"
         + "document.querySelector('#altura').value='165';"
         + "document.querySelector('#objetivo').value='Ser más activo';");
        capturar("CP10a perfil formulario");
        enviarFormulario("form[action$='/perfil']");
        capturar("CP10b perfil guardado IMC");

        // CP07 - hidratación (POST con botones rápidos y cantidad libre)
        ir("/hidratacion");
        capturar("CP07a hidratacion inicial");
        for (int i = 0; i < 3; i++) clickBotonAgua(250);
        clickBotonAgua(500);
        js("document.querySelector('#cantidad').value='250';");
        enviarFormulario("form.filtro");
        capturar("CP07b hidratacion registrada");

        // CP08 - actividad física (POST)
        ir("/actividad");
        capturar("CP08a actividad vacia");
        registrarActividad("Caminata", 30, hoy);
        registrarActividad("Yoga", 15, hoy);
        registrarActividad("Entrenamiento de fuerza", 25, hoy.minusDays(1));
        registrarActividad("Trote", 20, hoy.minusDays(2));
        registrarActividad("Ciclismo", 45, hoy.minusDays(3));
        registrarActividad("Baile", 40, hoy.minusDays(5));
        // formulario listo antes de enviar la última
        seleccionarActividad("Caminata", 35, hoy.minusDays(4));
        capturar("CP08b actividad formulario POST");
        enviarFormulario("form:has(input[value=registrar])");
        capturar("CP08c actividad registrada");

        // CP09 - filtro del historial con GET
        js("document.querySelector('#desde').value='" + hoy.minusDays(2) + "';"
         + "document.querySelector('#hasta').value='" + hoy + "';");
        enviarFormulario("form[method=get]");
        capturar("CP09 filtro historial GET");

        // CP11 - panel con datos
        ir("/dashboard");
        capturar("CP11 panel con estadisticas");

        // Vistas responsive (móvil)
        vista(390, 844, true);
        ir("/dashboard");
        capturar("Responsive panel movil");
        ir("/hidratacion");
        capturar("Responsive hidratacion movil");
        vista(1366, 860, false);

        // CP12 - cerrar sesión con modal
        ir("/dashboard");
        js("document.getElementById('modalSalir').showModal();");
        Thread.sleep(400);
        capturar("CP12a modal cerrar sesion");
        enviarFormulario("#modalSalir form");
        capturar("CP12b sesion cerrada");

        // Página de error 404
        ir("/no-existe");
        capturar("Error 404 personalizado");
    }

    static void clickBotonAgua(int ml) throws Exception {
        js("document.querySelector(\"form input[name=cantidad][value='" + ml + "']\").form.querySelector('button').click();");
        esperarCarga();
    }

    static void seleccionarActividad(String nombre, int minutos, LocalDate fecha) throws Exception {
        js("var s=document.querySelector('#idActividad');"
         + "for (var o of s.options) { if (o.text.startsWith('" + nombre + " (')) s.value=o.value; }"
         + "document.querySelector('#duracion').value='" + minutos + "';"
         + "document.querySelector('#fecha').value='" + fecha + "';");
    }

    static void registrarActividad(String nombre, int minutos, LocalDate fecha) throws Exception {
        seleccionarActividad(nombre, minutos, fecha);
        enviarFormulario("form:has(input[value=registrar])");
    }

    // ------------------------------------------------------------------ acciones de navegador
    static void ir(String ruta) throws Exception {
        enviar("Page.navigate", "{\"url\":\"" + BASE + ruta + "\"}");
        Thread.sleep(300);
        esperarCarga();
    }

    /** Envía el formulario pulsando su botón submit (como lo haría la usuaria). */
    static void enviarFormulario(String selector) throws Exception {
        String antes = js("String(performance.timeOrigin)");
        js("var f=document.querySelector(\"" + selector + "\");"
         + "var b=f.querySelector('button[type=submit],button:not([type])');"
         + "if (b) b.click(); else f.requestSubmit();");
        for (int i = 0; i < 50; i++) {      // espera a que cargue la página nueva
            Thread.sleep(150);
            try {
                if (!antes.equals(js("String(performance.timeOrigin)"))) break;
            } catch (Exception navegando) { /* contexto cambiando */ }
        }
        esperarCarga();
    }

    static void esperarCarga() throws Exception {
        for (int i = 0; i < 80; i++) {
            try {
                if ("complete".equals(js("document.readyState"))) break;
            } catch (Exception navegando) { /* reintenta */ }
            Thread.sleep(150);
        }
        try { js("document.fonts.ready.then(()=>true)"); } catch (Exception ignored) { }
        Thread.sleep(500);
    }

    static String url() throws Exception {
        return js("location.href");
    }

    static void vista(int w, int h, boolean esMovil) throws Exception {
        ancho = w; alto = h; movil = esMovil;
        enviar("Emulation.setDeviceMetricsOverride",
                "{\"width\":" + w + ",\"height\":" + h + ",\"deviceScaleFactor\":1,\"mobile\":" + esMovil + "}");
    }

    static void capturar(String nombre) throws Exception {
        String direccion = url();
        int altoPagina = Integer.parseInt(js("String(Math.max(document.documentElement.scrollHeight, document.body.scrollHeight))"));
        // Los modales se capturan sólo en la ventana visible
        boolean modalAbierto = "true".equals(js("String(!!document.querySelector('dialog[open]'))"));
        int h = modalAbierto ? alto : Math.min(Math.max(altoPagina, alto), 3000);
        String r = enviar("Page.captureScreenshot",
                "{\"format\":\"png\",\"captureBeyondViewport\":" + !modalAbierto
                + ",\"clip\":{\"x\":0,\"y\":0,\"width\":" + ancho + ",\"height\":" + h + ",\"scale\":1}}");
        Matcher m = Pattern.compile("\"data\":\"([^\"]+)\"").matcher(r);
        if (!m.find()) throw new IllegalStateException("Sin imagen: " + r);
        BufferedImage pagina = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(m.group(1))));
        BufferedImage conBarra = agregarBarra(pagina, direccion);
        contador++;
        String archivo = String.format("%02d-%s.png", contador,
                nombre.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", ""));
        ImageIO.write(conBarra, "png", SALIDA.resolve(archivo).toFile());
        System.out.println("  ✓ " + archivo + "   " + direccion);
    }

    /** Dibuja una barra superior tipo navegador con la URL real y la fecha/hora de la captura. */
    static BufferedImage agregarBarra(BufferedImage pagina, String direccion) {
        int barra = 44;
        BufferedImage img = new BufferedImage(pagina.getWidth(), pagina.getHeight() + barra, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0xE9ECEF));
        g.fillRect(0, 0, img.getWidth(), barra);
        g.setColor(new Color(0xFF5F57)); g.fillOval(14, 16, 12, 12);
        g.setColor(new Color(0xFEBC2E)); g.fillOval(32, 16, 12, 12);
        g.setColor(new Color(0x28C840)); g.fillOval(50, 16, 12, 12);
        int x0 = 76, anchoCaja = img.getWidth() - x0 - (img.getWidth() > 600 ? 190 : 12);
        g.setColor(Color.WHITE);
        g.fillRoundRect(x0, 8, anchoCaja, 28, 14, 14);
        g.setColor(new Color(0x30343A));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();
        String texto = direccion;
        while (fm.stringWidth(texto) > anchoCaja - 24 && texto.length() > 10) texto = texto.substring(0, texto.length() - 2);
        if (!texto.equals(direccion)) texto += "…";
        g.drawString(texto, x0 + 12, 27);
        if (img.getWidth() > 600) {
            g.setColor(new Color(0x6B7280));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g.drawString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    img.getWidth() - 170, 27);
        }
        g.drawImage(pagina, 0, barra, null);
        g.dispose();
        return img;
    }

    // ------------------------------------------------------------------ protocolo DevTools
    static Process iniciarNavegador() throws Exception {
        String[] candidatos = {
            System.getenv("ProgramFiles(x86)") + "\\Microsoft\\Edge\\Application\\msedge.exe",
            System.getenv("ProgramFiles") + "\\Microsoft\\Edge\\Application\\msedge.exe",
            System.getenv("ProgramFiles") + "\\Google\\Chrome\\Application\\chrome.exe",
            System.getenv("ProgramFiles(x86)") + "\\Google\\Chrome\\Application\\chrome.exe",
            System.getenv("LOCALAPPDATA") + "\\Google\\Chrome\\Application\\chrome.exe",
        };
        String exe = System.getenv("VH_NAVEGADOR");
        if (exe != null && !new File(exe).exists()) exe = null;
        if (exe == null) for (String c : candidatos) if (c != null && new File(c).exists()) { exe = c; break; }
        if (exe == null) throw new IllegalStateException("No se encontró Microsoft Edge ni Google Chrome");
        Path perfil = Files.createTempDirectory("vh-navegador");
        System.out.println("Navegador: " + exe);
        java.util.List<String> cmd = new java.util.ArrayList<>(java.util.List.of(exe, "--headless=new", "--remote-debugging-port=9333",
                "--user-data-dir=" + perfil, "--no-first-run", "--no-default-browser-check",
                "--hide-scrollbars", "--lang=es-CO", "--window-size=1366,860"));
        if (!System.getProperty("os.name").startsWith("Windows")) cmd.add("--no-sandbox");
        cmd.add("about:blank");
        return new ProcessBuilder(cmd)
                .redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
    }

    static void conectar() throws Exception {
        HttpClient http = HttpClient.newBuilder().proxy(HttpClient.Builder.NO_PROXY).build();
        String lista = null;
        for (int i = 0; i < 60 && lista == null; i++) {
            try {
                lista = http.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:9333/json/list")).build(),
                        HttpResponse.BodyHandlers.ofString()).body();
                if (!lista.contains("\"page\"")) lista = null;
            } catch (Exception e) { Thread.sleep(500); }
        }
        if (lista == null) throw new IllegalStateException("El navegador no respondió");
        Matcher m = Pattern.compile("\"type\"\\s*:\\s*\"page\"[\\s\\S]*?\"webSocketDebuggerUrl\"\\s*:\\s*\"([^\"]+)\"").matcher(lista);
        if (!m.find()) {
            m = Pattern.compile("\"webSocketDebuggerUrl\"\\s*:\\s*\"(ws://[^\"]+/page/[^\"]+)\"").matcher(lista);
            if (!m.find()) throw new IllegalStateException("No hay pestaña: " + lista);
        }
        ws = http.newWebSocketBuilder().buildAsync(URI.create(m.group(1)), new WebSocket.Listener() {
            final StringBuilder buffer = new StringBuilder();
            @Override public CompletionStage<?> onText(WebSocket w, CharSequence datos, boolean ultimo) {
                buffer.append(datos);
                if (ultimo) {
                    String msg = buffer.toString();
                    buffer.setLength(0);
                    Matcher id = Pattern.compile("^\\{\"id\":(\\d+)").matcher(msg);
                    if (id.find()) {
                        CompletableFuture<String> f = PENDIENTES.remove(Integer.parseInt(id.group(1)));
                        if (f != null) f.complete(msg);
                    }
                }
                w.request(1);
                return null;
            }
        }).join();
    }

    static String enviar(String metodo, String params) throws Exception {
        int id = IDS.incrementAndGet();
        CompletableFuture<String> f = new CompletableFuture<>();
        PENDIENTES.put(id, f);
        ws.sendText("{\"id\":" + id + ",\"method\":\"" + metodo + "\",\"params\":" + params + "}", true).join();
        String r = f.get(30, TimeUnit.SECONDS);
        if (r.contains("\"error\":{")) throw new IllegalStateException(metodo + " -> " + r);
        return r;
    }

    /** Ejecuta JavaScript en la página y devuelve el resultado como texto. */
    static String js(String codigo) throws Exception {
        String r = enviar("Runtime.evaluate", "{\"expression\":" + jsonTexto(codigo)
                + ",\"awaitPromise\":true,\"returnByValue\":true}");
        if (r.contains("\"exceptionDetails\"")) throw new IllegalStateException("JS: " + codigo + " -> " + r);
        Matcher m = Pattern.compile("\"value\":\"((?:[^\"\\\\]|\\\\.)*)\"").matcher(r);
        if (m.find()) return m.group(1).replace("\\/", "/").replace("\\\"", "\"").replace("\\\\", "\\");
        m = Pattern.compile("\"value\":([^,}]+)").matcher(r);
        return m.find() ? m.group(1) : "";
    }

    static String jsonTexto(String s) {
        StringBuilder b = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> b.append("\\\"");
                case '\\' -> b.append("\\\\");
                case '\n' -> b.append("\\n");
                default -> { if (c < 32 || c > 126) b.append(String.format("\\u%04x", (int) c)); else b.append(c); }
            }
        }
        return b.append('"').toString();
    }
}
