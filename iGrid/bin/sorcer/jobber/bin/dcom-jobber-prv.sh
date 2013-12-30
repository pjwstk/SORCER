#!/bin/ksh
echo "[Run] Starting  SORCER Jobber..."

JINTEGRA_BASEDIR=${SORCER_BASEDIR}/jSorcer/jintegra
JINTEGRA_CODEBASE=${JINTEGRA_BASEDIR}/lib:${JINTEGRA_BASEDIR}/lib/jintegra.jar
CLASSPATH=${JINTEGRA_CODEBASE}:${CLASSPATH}

java -Djava.security.policy=policy \
     -DJINTEGRA_DCOM_PORT=1355 \
     -Djava.compiler=NONE \
	-Djava.security.manager \
	-Djava.security.policy=${SORCER_HOME}/policy/policy \
	-Djava.security.aith.policy=${SORCER_HOME}/policy/policy.jaas \
	-Djava.security.auth.login.config=${SORCER_HOME}/policy/login.conf \
        -Dportal.server=http://${SORCER_PORTAL_HOST}:${SORCER_PORTAL_PORT}/Sorcer/servlet/server \
     Sorcer.service.jobber.DCOMJobberServer $1
