@echo off

if not exist "C:\DndZeta" (
    mkdir C:\DndZeta
    set "PATH_NOT_EXIST=true"
)

cd C:\DndZeta

rem Define project name for PaperMC
set "PROJECT=paper"

rem Get the latest Minecraft version available for PaperMC
for /f "tokens=*" %%i in ('curl -s https://api.papermc.io/v2/projects/%PROJECT% ^| jq -r ".versions[-1]"') do set "LATEST_VERSION=%%i"
echo Latest Minecraft version is %LATEST_VERSION%

rem Get the latest build (including unstable) for the latest version
for /f "tokens=*" %%i in ('curl -s https://api.papermc.io/v2/projects/%PROJECT%/versions/%LATEST_VERSION%/builds ^| jq -r ".builds[-1].build"') do set "LATEST_BUILD=%%i"

if "%LATEST_BUILD%"=="null" (
    echo No build found for version %LATEST_VERSION%.
    exit /b
)

rem -----------------setup paper server----------------
echo Latest build is %LATEST_BUILD%

rem Construct the download URL and download the .jar file
set "JAR_NAME=%PROJECT%-%LATEST_VERSION%-%LATEST_BUILD%.jar"
set "DOWNLOAD_URL=https://api.papermc.io/v2/projects/%PROJECT%/versions/%LATEST_VERSION%/builds/%LATEST_BUILD%/downloads/%JAR_NAME%"

curl -o server.jar %DOWNLOAD_URL%
echo Download complete: server.jar

if defined PATH_NOT_EXIST (
    echo eula=true > eula.txt
)
