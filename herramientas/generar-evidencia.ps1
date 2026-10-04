<#
  VitaHealth - Ejecuta todo el ciclo y deja la evidencia:
    1. Prepara el entorno portátil (JDK, Tomcat, librerías)
    2. Ejecuta las pruebas JUnit (captura de pantalla de la consola)
    3. Compila, empaqueta el WAR y despliega en Tomcat (captura de la consola de Tomcat)
    4. Ejecuta la prueba funcional automatizada CP01-CP12 y guarda una captura por paso
  Las imágenes quedan en la carpeta  ..\capturas  (al lado del proyecto).
#>
$ErrorActionPreference = 'Stop'
$PROY = Split-Path $PSScriptRoot -Parent
$CAP  = Join-Path (Split-Path $PROY -Parent) 'capturas'
$ENT  = Join-Path $env:USERPROFILE 'vitahealth-entorno'
New-Item -ItemType Directory -Force $CAP | Out-Null
Start-Transcript -Path (Join-Path $CAP 'registro-ejecucion.log') -Force | Out-Null

Add-Type -AssemblyName System.Windows.Forms, System.Drawing
Add-Type @"
using System; using System.Runtime.InteropServices;
public static class Dpi { [DllImport("user32.dll")] public static extern bool SetProcessDPIAware(); }
"@
[Dpi]::SetProcessDPIAware() | Out-Null

function Capturar-Pantalla([string]$nombre) {
    Start-Sleep -Milliseconds 800
    $b = [System.Windows.Forms.Screen]::PrimaryScreen.Bounds
    $bmp = New-Object System.Drawing.Bitmap $b.Width, $b.Height
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.CopyFromScreen($b.Location, [System.Drawing.Point]::Empty, $b.Size)
    $bmp.Save((Join-Path $CAP $nombre), [System.Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose(); $bmp.Dispose()
    Write-Host "  [captura] $nombre" -ForegroundColor DarkGray
}

Write-Host "`n===== 1. PREPARAR ENTORNO =====" -ForegroundColor Cyan
& "$PSScriptRoot\preparar-entorno.ps1"

Clear-Host
Write-Host "===== VitaHealth - Pruebas unitarias y de integracion (JUnit 5) =====" -ForegroundColor Cyan
Write-Host "Proyecto: $PROY`n"
& "$PSScriptRoot\ejecutar-pruebas.ps1"
$resultadoPruebas = $LASTEXITCODE
Capturar-Pantalla 'P01-pruebas-junit-consola.png'

Clear-Host
Write-Host "===== VitaHealth - Compilacion, empaquetado WAR y despliegue en Tomcat =====" -ForegroundColor Cyan
& "$PSScriptRoot\compilar-y-ejecutar.ps1"
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
Capturar-Pantalla 'P02-tomcat-desplegado.png'

Write-Host "`n===== 4. PRUEBA FUNCIONAL AUTOMATIZADA (CP01-CP12) =====" -ForegroundColor Cyan
& "$ENT\jdk\bin\java.exe" "-Dstdout.encoding=UTF-8" "$PSScriptRoot\capturas\CapturadorPantallas.java" $CAP
if ($LASTEXITCODE -ne 0) { throw "La prueba funcional falló" }

Write-Host "`nResultado pruebas JUnit (0 = todas pasaron): $resultadoPruebas" -ForegroundColor Green
Write-Host "Evidencia en: $CAP" -ForegroundColor Green
Write-Host "La aplicacion sigue corriendo en http://localhost:8080/vitahealth/"
Stop-Transcript | Out-Null
