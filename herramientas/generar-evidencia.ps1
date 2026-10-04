<#
  VitaHealth - Ejecuta todo el ciclo y deja la evidencia:
    1. Prepara el entorno portátil (JDK, Tomcat, librerías)
    2. Ejecuta las pruebas JUnit (salida guardada en capturas)
    3. Compila, empaqueta el WAR y despliega en Tomcat (log de Tomcat guardado)
    4. Ejecuta la prueba funcional automatizada CP01-CP12 y guarda una captura por paso
  Las imágenes quedan en la carpeta  ..\capturas  (al lado del proyecto).
#>
$ErrorActionPreference = 'Stop'
$PROY = Split-Path $PSScriptRoot -Parent
$CAP  = Join-Path (Split-Path $PROY -Parent) 'capturas'
$ENT  = Join-Path $env:USERPROFILE 'vitahealth-entorno'
New-Item -ItemType Directory -Force $CAP | Out-Null
Start-Transcript -Path (Join-Path $CAP 'registro-ejecucion.log') -Force | Out-Null

# Ejecución limpia: se borran capturas anteriores y la base de datos local de pruebas
Get-ChildItem $CAP -Filter *.png -ErrorAction SilentlyContinue | Remove-Item -Force
if (Test-Path "$ENT\tomcat\bin\shutdown.bat") { cmd.exe /c "`"$ENT\tomcat\bin\shutdown.bat`" >nul 2>&1"; Start-Sleep -Seconds 3 }
$BD = Join-Path $env:USERPROFILE 'vitahealth-db'
if (Test-Path $BD) { Remove-Item $BD -Recurse -Force }

Write-Host "`n===== 1. PREPARAR ENTORNO =====" -ForegroundColor Cyan
& "$PSScriptRoot\preparar-entorno.ps1"

Clear-Host
Write-Host "===== VitaHealth - Pruebas unitarias y de integracion (JUnit 5) =====" -ForegroundColor Cyan
Write-Host "Proyecto: $PROY`n"
& "$PSScriptRoot\ejecutar-pruebas.ps1" | Tee-Object -FilePath (Join-Path $CAP 'salida-pruebas-junit.txt')
$resultadoPruebas = $LASTEXITCODE

Clear-Host
Write-Host "===== VitaHealth - Compilacion, empaquetado WAR y despliegue en Tomcat =====" -ForegroundColor Cyan
& "$PSScriptRoot\compilar-y-ejecutar.ps1" | Tee-Object -FilePath (Join-Path $CAP 'salida-compilacion-despliegue.txt')
Write-Host "Esperando a que Tomcat responda..."
$ok = $false
for ($i = 0; $i -lt 60 -and -not $ok; $i++) {
    try {
        $r = Invoke-WebRequest 'http://localhost:8080/vitahealth/' -UseBasicParsing -TimeoutSec 3
        if ($r.StatusCode -eq 200) { $ok = $true }
    } catch { Start-Sleep -Seconds 2 }
}
if (-not $ok) { throw "Tomcat no respondió en http://localhost:8080/vitahealth/" }
Write-Host "Tomcat respondio HTTP 200 en http://localhost:8080/vitahealth/" -ForegroundColor Green
Start-Sleep -Seconds 2
Get-ChildItem "$ENT\tomcat\logs" -Filter 'catalina.*.log' | Sort-Object LastWriteTime | Select-Object -Last 1 |
    ForEach-Object { Copy-Item $_.FullName (Join-Path $CAP 'salida-tomcat-catalina.log') -Force }

Write-Host "`n===== 4. PRUEBA FUNCIONAL AUTOMATIZADA (CP01-CP12) =====" -ForegroundColor Cyan
& "$ENT\jdk\bin\java.exe" "-Dstdout.encoding=UTF-8" "$PSScriptRoot\capturas\CapturadorPantallas.java" $CAP |
    Tee-Object -FilePath (Join-Path $CAP 'salida-prueba-funcional.txt')
if ($LASTEXITCODE -ne 0) { throw "La prueba funcional falló" }

Write-Host "`nResultado pruebas JUnit (0 = todas pasaron): $resultadoPruebas" -ForegroundColor Green
Write-Host "Evidencia en: $CAP" -ForegroundColor Green
Write-Host "La aplicacion sigue corriendo en http://localhost:8080/vitahealth/"
Stop-Transcript | Out-Null
