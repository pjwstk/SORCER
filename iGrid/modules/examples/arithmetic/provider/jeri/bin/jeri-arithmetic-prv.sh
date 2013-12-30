#!/bin/sh

WEBSTER_URL="http://${IGRID_WEBSTER}:${IGRID_WEBSTER_PORT}"

java -Djava.security.manager= \
    -Djava.util.logging.config.file=${IGRID_HOME}/configs/sorcer.logging \
    -Djava.security.policy=../policy/jeri-arithmetic-prv.policy \
    -Dprovider.codebase="${WEBSTER_URL}/arithmetic-dl.jar ${WEBSTER_URL}/sdm-dl.jar" \
    -Dprovider.classpath="${IGRID_HOME}/lib/jeri-arithemtic.jar:${CLASSPATH}" \
    -Dprovider.impl="sorcer.arithmetic.provider.jeri.ArithmeticProviderRemoteImpl" \
    -jar $IGRID_HOME/common/start.jar ../configs/start-jeri-arithmetic-remote-prv.config
