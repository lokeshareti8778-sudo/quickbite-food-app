@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup script for Windows.
@REM ----------------------------------------------------------------------------
@IF "%DEBUG%"=="" @ECHO OFF

set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

set WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
if not exist "%WRAPPER_JAR%" (
  echo Maven Wrapper JAR is missing: %WRAPPER_JAR% 1>&2
  exit /b 1
)

set JAVA_EXE=java.exe
if not "%JAVA_HOME%"=="" if exist "%JAVA_HOME%\bin\java.exe" set JAVA_EXE=%JAVA_HOME%\bin\java.exe

"%JAVA_EXE%" %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
if ERRORLEVEL 1 exit /b %ERRORLEVEL%
