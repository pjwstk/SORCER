echo SORCER Service Browser

JAVA_HOME=/usr/java/jdk1.6.0_10
JINILIB=/home/sobolemw/jini2_1/lib
POLICY=-Djava.security.policy=/home/sobolemw/incax/browser/policy.all
REQUESTOR_DATA="-Ddata.server=10.131.5.90 -Ddata.server.port=9000 -Ddata.root.dir=${IGRID_HOME}/data -Drequestor.data.dir=optimization/input"
CP=/home/sobolemw/incax/browser/incax-ce-browser.jar:/home/sobolemw/incax/browser/lib/serviceui.jar:$JINILIB/jsk-platform.jar:$JINILIB/jini-ext.jar:$JINILIB/sun-util.jar
$JAVA_HOME/bin/java $REQUESTOR_DATA -Djava.protocol.handler.pkgs=net.jini.url -cp $CP $POLICY sorcer.ui.SorcerServiceBrowser $1
