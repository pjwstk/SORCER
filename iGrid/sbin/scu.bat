@echo off
::IGRID_HOME_CYG=cygdrive %IGRID_HOME%
::bash -c "export IGRID_HOME=`cygpath \"%IGRID_HOME%\"`; echo \"IGRID_HOME = $IGRID_HOME\";slp %*"
::bash -c "export IGRID_HOME=\"%IGRID_HOME%\"; echo \"IGRID_HOME = $IGRID_HOME\";slp %*"
bash -c "export IGRID_HOME2=`cygpath \"%IGRID_HOME%\"`; source $IGRID_HOME2/configs/minClasspath;export CLASSPATH=${CLASSPATH_WIN};scu %*"

