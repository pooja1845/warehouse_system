@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF)
@REM Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------
@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET PN=%__MVNW_ARG0_NAME__%
@SET PATCHLEVEL=0

@IF NOT "%MVNW_VERBOSE%"=="true" (SETLOCAL DisableDelayedExpansion)
@SET MVNW_REPOURL=https://repo.maven.apache.org/maven2

@SET WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@SET DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

@FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
    @IF "%%A"=="wrapperUrl" SET DOWNLOAD_URL=%%B
)

@SETLOCAL EnableExtensions KeepDelayedExpansion
@IF NOT EXIST %WRAPPER_JAR% (
    @IF NOT "%MVNW_REPOURL%"=="" (
        @SET DOWNLOAD_URL="%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
    )
    @SET MVNW_VERBOSE=true
)

@SET MAVEN_OPTS=!MAVEN_OPTS!
@SET MAVEN_DEBUG_OPTS=!MAVEN_DEBUG_OPTS!
@SET MAVEN_PROJECTBASEDIR=%~dp0

@java -cp %WRAPPER_JAR% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %MAVEN_CONFIG% %*
@IF ERRORLEVEL 1 GOTO end
:end
@ENDLOCAL
