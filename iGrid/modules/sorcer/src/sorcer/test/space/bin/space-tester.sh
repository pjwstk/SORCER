#!/bin/sh

WEBSTER="http://${IGRID_WEBSTER}:${IGRID_WEBSTER_PORT}"

echo "Webster: ${WEBSTER}"

REQUESTOR_NAME="spaceTester"
REQUESTOR_CLASS="sorcer.test.space.SpaceTester"

JINI_JARS="${IGRID_HOME}/lib/sorcer.jar:${IGRID_HOME}/lib/jgapp.jar:${IGRID_HOME}/common/jini-ext.jar:${IGRID_HOME}/common/sun-util.jar:${IGRID_HOME}/common/serviceui-1.1.jar"

java -classpath ${JINI_JARS}:${IGRID_HOME}/lib/${REQUESTOR_NAME}.jar \
	 -Djava.util.logging.config.file="${IGRID_HOME}/configs/sorcer.logging" \
	 -Djava.security.policy="../policy/${REQUESTOR_NAME}-req.policy" \
	 -Djava.rmi.server.codebase="${WEBSTER}/${REQUESTOR_NAME}-dl.jar" \
     -Dsorcer.env.file="${IGRID_HOME}/configs/sorcer.env" \
     ${REQUESTOR_CLASS}