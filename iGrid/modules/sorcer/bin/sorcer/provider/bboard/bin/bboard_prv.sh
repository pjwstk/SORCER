#!/bin/sh

JINI_BASEURL="http://${JINI_HTTP_HOST}:${JINI_HTTP_PORT}"
IGRID_URL="http://${IGRID_WEBSTER}:${IGRID_WEBSTER_PORT}"


CODEBASE="${IGRID_URL}/bboard-dl.jar ${JINI_BASEURL}/sdm-dl.jar"
APP_BASE="${IGRID_HOME}/modules/wservices/doc"

echo class server ${IGRID_URL}
echo " "

 java \
-Djava.security.manager= \
-Djava.util.logging.config.file=./../config/bboard.logging \
-Djava.security.policy=./../policy/bboard.policy \
-Djavax.net.ssl.trustStore=./../config/truststore.server \
-Djavax.net.ssl.trustStorePassword=client \
-Dprovider.codebase="${IGRID_URL}/bboard-dl.jar ${IGRID_URL}/bboard-ui.jar" \
-Dprovider.classpath=./../lib/bboard.jar \
-Dprovider.impl=sorcer.provider.bboard.BBoardProviderImpl \
-Dprovider.properties=./../config/bboard.properties \
-Djava.security.auth.login.config=./../config/serverjaas.config \
-jar ${JINI_HOME}/lib/start.jar ./../config/start-bboard.config

