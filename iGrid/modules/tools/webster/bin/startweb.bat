@ECHO OFF
REM startweb.bat
REM ANT_HOME=C:\ant\apache-ant-1.7.0
REM JAVA_HOME="C:\Program Files\Java\jdk1.6.0_16"
REM PATH=%PATH%;%JAVA_HOME%\bin;%ANT_HOME\bin
echo Running WEBSTER
cd %IGRID_HOME%\bin\webster\bin
ant -f webster-run.xml
PAUSE
REM EOF
