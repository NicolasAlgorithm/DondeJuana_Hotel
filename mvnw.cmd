@ECHO OFF
SETLOCAL

set ERROR_CODE=0
set MAVEN_PROJECTBASEDIR=%~dp0

if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

set WRAPPER_DIR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper
set WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
set WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar

if not exist "%WRAPPER_DIR%" (
  mkdir "%WRAPPER_DIR%"
)

if not exist "%WRAPPER_JAR%" (
  echo Downloading Maven Wrapper jar...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')"
  if ERRORLEVEL 1 (
    echo Failed to download Maven Wrapper jar from %WRAPPER_URL%
    exit /b 1
  )
)

if "%JAVA_HOME%"=="" (
  set JAVA_EXE=java
) else (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*
set ERROR_CODE=%ERRORLEVEL%

ENDLOCAL & set ERROR_CODE=%ERROR_CODE%
exit /b %ERROR_CODE%
