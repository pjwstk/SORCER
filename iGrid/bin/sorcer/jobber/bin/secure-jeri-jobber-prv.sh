#!/bin/ksh

SORCER_BASEURL="http://${IGRID_PORTAL_HOST}:${IGRID_PORTAL_PORT}"
JINI_BASEURL="http://${IGRID_WEBSTER}:${IGRID_WEBSTER_PORT}"

echo "${SORCER_BASEURL}/jobber-dl.jar ${JINI_BASEURL}/sdm-dl.jar"

java -Djava.security.manager= \
    -Djava.protocol.handler.pkgs=net.jini.url \
    -Djavax.net.ssl.trustStore=../configs/truststore.server \
    -Djavax.net.ssl.keyStore=../configs/keyStore.server \
    -Djava.security.auth.login.config=../configs/serverjaas.config \
    -Djava.util.logging.config.file=${IGRID_HOME}/configs/sorcer.logging \
    -Djava.security.policy=../policy/jobber-prv.policy \
    -Dsorcer.server.codebase="${SORCER_BASEURL}/jobber-dl.jar ${JINI_BASEURL}/sdm-dl.jar ${SORCER_BASEURL}/sorcer.jar" \
    -Dserver.classpath="${IGRID_HOME}/lib/jobber.jar" \
    -Dserver.impl="sorcer.core.provider.jobber.JobberImpl" \
    -jar $JINI_HOME/lib/start.jar ../configs/secure-start-jobber-prv.config

