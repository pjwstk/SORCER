#!/bin/sh

# Replace MYPROVIDER with your provider name and specify property value for "PROVIDER_CLASS"
# Also you might need to adjust the classpath (provider.classpath) for your provider.

WEBSTER="http://${IGRID_WEBSTER}:${IGRID_WEBSTER_PORT}"

PROVIDER_NAME="methoder"
PROVIDER_CLASS="sorcer.core.grid.provider.methoder.MethoderImpl"

JINI_JARS="${IGRID_HOME}/common/sun-util.jar:${IGRID_HOME}/common/jini-ext.jar:${IGRID_HOME}/common/serviceui-1.1.jar"
SORCER_JARS="${IGRID_HOME}/lib/sorcer.jar"
JINI_DL="${WEBSTER}/jini-ext.jar ${WEBSTER}/serviceui-1.1.jar ${WEBSTER}/sun-util.jar"

echo "IGRID_HOME: ${IGRID_HOME}"
echo "Webster: ${WEBSTER}"

java -Djava.security.manager= \
     -Djava.util.logging.config.file=${IGRID_HOME}/configs/sorcer.logging \
     -Djava.security.policy=../policy/${PROVIDER_NAME}-prv.policy \
     -Dsorcer.provider.codebase="${JINI_DL} ${WEBSTER}/${PROVIDER_NAME}-dl.jar" \
     -Dsorcer.provider.classpath="${JINI_JARS}:${SORCER_JARS}:${IGRID_HOME}/lib/${PROVIDER_NAME}.jar" \
     -Dsorcer.env.file="${IGRID_HOME}/configs/sorcer.env" \
     -Dsorcer.provider.config="../configs/jeri-${PROVIDER_NAME}-prv.config" \
     -Dsorcer.provider.impl="${PROVIDER_CLASS}" \
     -jar ${IGRID_HOME}/common/start.jar ${IGRID_HOME}/configs/startup-prv.config
     
