@echo off
bash -c "export IGRID_HOME2=`cygpath \"%IGRID_HOME%\"`; source $IGRID_HOME2/configs/minClasspath;export CLASSPATH=${CLASSPATH_WIN};smc %*"

