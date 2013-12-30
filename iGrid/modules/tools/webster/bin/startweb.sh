#!/bin/bash

# This scripts starts a Webster process set the serve up code using the org.jini.rio.tools.webster.root system
# property

$JAVA_HOME/bin/java -jar -Djava.protocol.handler.pkgs=net.jini.url \
	-Dorg.jini.rio.tools.webster.debug \
	-Dorg.jini.rio.tools.webster.root="$IGRID_HOME/lib;$IGRID_HOME/common;$IGRID_HOME/common/jaxta;$IGRID_HOME/classes" \
	-Dorg.jini.rio.tools.webster.port=$IGRID_WEBSTER_PORT \
	-Djava.security.policy=$IGRID_HOME/policy/policy.all \
	$IGRID_HOME/common/rio3_2/webster.jar




