<#
  VitaHealth - Compila el proyecto, arma la aplicación web (WAR) y la despliega en Tomcat.
  Requiere haber ejecutado antes preparar-entorno.ps1
  Uso:  powershell -ExecutionPolicy Bypass -File herramientas\compilar-y-ejecutar.ps1 [-Detener]
  Luego abrir: http://localhost:8080/vitahealth/
#>
param([switch]$Detener)
$ErrorActionPreference = 'Stop'

$ENT  = Join-Path $env:USERPROFILE 'vitahealth-entorno'
$PROY = Split-Path $PSScriptRoot -Parent
$env:JAVA_HOME = "$ENT\jdk"
$env:CATALINA_HOME = "$ENT\tomcat"
$JAVAC = "$ENT\jdk\bin\javac.exe"
$JAR   = "$ENT\jdk\bin\jar.exe"

if (-not (Test-Path $JAVAC)) { throw "No se encontró el JDK. Ejecuta primero herramientas\preparar-entorno.ps1" }

# Detiene Tomcat si está corriendo
cmd.exe /c "`"$ENT\tomcat\bin\shutdown.bat`" >nul 2>&1"
Start-Sleep -Seconds 2
if ($Detener) { Write-Host "Tomcat detenido."; exit 0 }

$BUILD   = Join-Path $ENT 'build\vitahealth'
$CLASSES = Join-Path $BUILD 'WEB-INF\classes'
if (Test-Path $BUILD) { Remove-Item $BUILD -Recurse -Force }
New-Item -ItemType Directory -Force $CLASSES, "$BUILD\WEB-INF\lib" | Out-Null

Write-Host "== Copiando vistas (JSP, CSS, imágenes) ==" -ForegroundColor Green
Copy-Item "$PROY\src\main\webapp\*" $BUILD -Recurse -Force

Write-Host "== Compilando clases Java (servlets, DAO, modelo) ==" -ForegroundColor Green
$fuentes = Get-ChildItem "$PROY\src\main\java" -Recurse -Filter *.java |
           ForEach-Object { '"' + ($_.FullName -replace '\\','/') + '"' }
$argfile = Join-Path $ENT 'build\fuentes.txt'
Set-Content -Path $argfile -Value $fuentes -Encoding ASCII
$cp = "$ENT\tomcat\lib\servlet-api.jar;$ENT\tomcat\lib\jsp-api.jar"
& $JAVAC -encoding UTF-8 -Xlint:unchecked -d $CLASSES -cp $cp "@$argfile"
if ($LASTEXITCODE -ne 0) { throw "Error de compilación" }
Copy-Item "$PROY\src\main\resources\*" $CLASSES -Force

Write-Host "== Copiando librerías (JSTL, H2, PostgreSQL) ==" -ForegroundColor Green
Get-ChildItem "$ENT\lib\*.jar" | Where-Object { $_.Name -notlike 'junit*' } |
    ForEach-Object { Copy-Item $_.FullName "$BUILD\WEB-INF\lib" }

Write-Host "== Empaquetando vitahealth.war ==" -ForegroundColor Green
New-Item -ItemType Directory -Force "$PROY\target" | Out-Null
& $JAR -cf "$PROY\target\vitahealth.war" -C $BUILD .

Write-Host "== Desplegando en Tomcat ==" -ForegroundColor Green
$destino = "$ENT\tomcat\webapps\vitahealth"
if (Test-Path $destino) { Remove-Item $destino -Recurse -Force }
Copy-Item $BUILD $destino -Recurse
& "$ENT\tomcat\bin\startup.bat"

Write-Host "`nListo. Abre http://localhost:8080/vitahealth/" -ForegroundColor Green
