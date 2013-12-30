#!/bin/sh

WEBSTER_URL="http://${IGRID_WEBSTER}:${IGRID_WEBSTER_PORT}"

java -Djava.security.manager= \
    -Djava.util.logging.config.file=${IGRID_HOME}/configs/sorcer.logging \
    -Djava.security.policy=../policy/policer-test-prv.policy \
    -Dprovider.codebase="${WEBSTER_URL}/policer-test-dl.jar ${WEBSTER_URL}/sdm-dl.jar" \
    -Dprovider.classpath="${IGRID_HOME}/lib/policer-test.jar:${CLASSPATH}" \
    -Dprovider.impl="sorcer.test.policer.src.ArithmeticProviderForPolicerImpl" \
    -jar $IGRID_HOME/common/start.jar ../configs/policer-test-prv.config
