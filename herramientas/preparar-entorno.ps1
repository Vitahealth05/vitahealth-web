<#
  VitaHealth - Prepara un entorno portátil (sin instalar nada como administrador):
    - JDK 21 (Eclipse Temurin)
    - Apache Tomcat 10.1 (Servlet 6.0 / JSP 3.1)
    - Librerías: JSTL 3.0, H2, PostgreSQL JDBC y JUnit 5 (consola)
  Todo queda en  %USERPROFILE%\vitahealth-entorno
  Uso:  powershell -ExecutionPolicy Bypass -File herramientas\preparar-entorno.ps1
#>
$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$ENT = Join-Path $env:USERPROFILE 'vitahealth-entorno'
$DES = Join-Path $ENT 'descargas'
$LIB = Join-Path $ENT 'lib'
New-Item -ItemType Directory -Force $ENT, $DES, $LIB | Out-Null
$M = 'https://repo1.maven.org/maven2'

function Descargar($url, $destino) {
    if (Test-Path $destino) { Write-Host "  ya existe: $(Split-Path $destino -Leaf)"; return }
    Write-Host "  descargando $(Split-Path $destino -Leaf) ..."
    Invoke-WebRequest -Uri $url -OutFile $destino -UseBasicParsing
}

function Descomprimir($zip, $destinoFinal) {
    $tmp = Join-Path $DES ('tmp-' + [IO.Path]::GetFileNameWithoutExtension($zip))
    if (Test-Path $tmp) { Remove-Item $tmp -Recurse -Force }
    New-Item -ItemType Directory $tmp | Out-Null
    tar.exe -xf $zip -C $tmp
    $raiz = Get-ChildItem $tmp -Directory | Select-Object -First 1
    Move-Item $raiz.FullName $destinoFinal
    Remove-Item $tmp -Recurse -Force
}

Write-Host "== 1/3 JDK 21 ==" -ForegroundColor Green
if (-not (Test-Path "$ENT\jdk\bin\javac.exe")) {
    Descargar 'https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jdk/hotspot/normal/eclipse' "$DES\jdk21.zip"
    Descomprimir "$DES\jdk21.zip" "$ENT\jdk"
}
& "$ENT\jdk\bin\java.exe" -version

Write-Host "== 2/3 Apache Tomcat 10.1 ==" -ForegroundColor Green
$TV = '10.1.34'
if (-not (Test-Path "$ENT\tomcat\bin\catalina.bat")) {
    Descargar "$M/org/apache/tomcat/tomcat/$TV/tomcat-$TV.zip" "$DES\tomcat-$TV.zip"
    Descomprimir "$DES\tomcat-$TV.zip" "$ENT\tomcat"
    # Se quitan las apps de ejemplo de Tomcat
    'docs','examples','host-manager','manager' | ForEach-Object {
        $p = "$ENT\tomcat\webapps\$_"; if (Test-Path $p) { Remove-Item $p -Recurse -Force }
    }
}
Write-Host "  Tomcat en $ENT\tomcat"

Write-Host "== 3/3 Librerías ==" -ForegroundColor Green
$librerias = [ordered]@{
    'jakarta.servlet.jsp.jstl-api-3.0.0.jar' = "$M/jakarta/servlet/jsp/jstl/jakarta.servlet.jsp.jstl-api/3.0.0/jakarta.servlet.jsp.jstl-api-3.0.0.jar"
    'jakarta.servlet.jsp.jstl-3.0.1.jar'     = "$M/org/glassfish/web/jakarta.servlet.jsp.jstl/3.0.1/jakarta.servlet.jsp.jstl-3.0.1.jar"
    'h2-2.2.224.jar'                         = "$M/com/h2database/h2/2.2.224/h2-2.2.224.jar"
    'postgresql-42.7.4.jar'                  = "$M/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar"
    'junit-platform-console-standalone-1.11.3.jar' = "$M/org/junit/platform/junit-platform-console-standalone/1.11.3/junit-platform-console-standalone-1.11.3.jar"
}
foreach ($nombre in $librerias.Keys) { Descargar $librerias[$nombre] (Join-Path $LIB $nombre) }

Write-Host "`nEntorno listo en $ENT" -ForegroundColor Green
