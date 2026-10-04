<#
  VitaHealth - Compila y ejecuta las pruebas unitarias y de integración (JUnit 5).
  Uso:  powershell -ExecutionPolicy Bypass -File herramientas\ejecutar-pruebas.ps1
#>
$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [Text.Encoding]::UTF8

$ENT  = Join-Path $env:USERPROFILE 'vitahealth-entorno'
$PROY = Split-Path $PSScriptRoot -Parent
$JDK  = "$ENT\jdk\bin"
$JUNIT = "$ENT\lib\junit-platform-console-standalone-1.11.3.jar"
$SALIDA = Join-Path $ENT 'build\pruebas'
if (Test-Path $SALIDA) { Remove-Item $SALIDA -Recurse -Force }
New-Item -ItemType Directory -Force $SALIDA | Out-Null

$fuentes = (Get-ChildItem "$PROY\src\main\java", "$PROY\src\test\java" -Recurse -Filter *.java |
            ForEach-Object { '"' + ($_.FullName -replace '\\','/') + '"' })
$argfile = Join-Path $ENT 'build\fuentes-pruebas.txt'
Set-Content -Path $argfile -Value $fuentes -Encoding ASCII

$cp = "$ENT\tomcat\lib\servlet-api.jar;$JUNIT;$ENT\lib\h2-2.2.224.jar"
& "$JDK\javac.exe" -encoding UTF-8 -d $SALIDA -cp $cp "@$argfile"
if ($LASTEXITCODE -ne 0) { throw "Error de compilación de pruebas" }
Copy-Item "$PROY\src\main\resources\*" $SALIDA -Force

New-Item -ItemType Directory -Force "$PROY\target" | Out-Null
& "$JDK\java.exe" "-Dstdout.encoding=UTF-8" -jar $JUNIT execute `
    --class-path "$SALIDA;$ENT\lib\h2-2.2.224.jar" --scan-class-path `
    --details=tree --disable-banner --disable-ansi-colors --reports-dir "$PROY\target\reportes-pruebas"
exit $LASTEXITCODE
