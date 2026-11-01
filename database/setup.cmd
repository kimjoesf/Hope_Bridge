@echo off
rem ---------------------------------------------------------------
rem  Builds the HopeBridge database from scratch with sqlcmd.
rem  Usage:  setup.cmd                 (uses localhost on port 1434)
rem          setup.cmd "tcp:MYPC,1433" (any other server)
rem  You can also open the three .sql files in SSMS and run them in order.
rem ---------------------------------------------------------------
setlocal
set SERVER=%~1
if "%SERVER%"=="" set SERVER=tcp:127.0.0.1,1434

cd /d "%~dp0"
for %%f in (01_create_database.sql 02_schema.sql 03_seed.sql) do (
    echo Running %%f ...
    sqlcmd -S %SERVER% -E -C -b -i %%f || goto :failed
)
echo.
echo Done. The HopeBridge database is ready.
exit /b 0

:failed
echo.
echo Something went wrong while running the scripts above.
exit /b 1
