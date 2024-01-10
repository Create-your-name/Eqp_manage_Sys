@echo off
rem ---------------------------------------------------------------------------
rem Start script for running the Timer(APC-PCM)
rem
rem ---------------------------------------------------------------------------

echo Start script for running the Timer(APC-PCM)
rem store the current directory
set CURRENT_DIR=%cd%

set TIMER_HOME=%cd%
echo using TimerHome Home %TIMER_HOME%

rem set the classes
cd %CURRENT_DIR%

rem loop through the libs and add them to the class path

setlocal EnableDelayedExpansion

set TIMER_CLASS_PATH=%TIMER_HOME%
FOR %%c in (%TIMER_HOME%\lib\*.jar) DO set TIMER_CLASS_PATH=!TIMER_CLASS_PATH!;%%c
set TIMER_HOME=%TIMER_HOME%

rem echo %TIMER_CLASS_PATH%
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8044 -classpath %TIMER_CLASS_PATH% -DService=%Service% -DNetwork=%Network% -DDaemon=%Daemon% -DDomain=%Domain% com.csmc.RunJob

rem java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8044 -classpath %TIMER_CLASS_PATH% com.csmc.syncpcmdata.job.SyncPcmSunmmaryParam

rem java -classpath %TIMER_CLASS_PATH% com.csmc.RunJob

endlocal

